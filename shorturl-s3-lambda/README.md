# Serverless function for ShortURL service

This Micronaut serverless function implements the ShortURL service specification using [AWS Lambda](https://aws.amazon.com/lambda/) and the [Amazon S3 object storage](https://aws.amazon.com/s3/).

## Endpoints

The only endpoint is the one in the [ShortURL service spec](../shorturl-api).

## Building the function

To build the uber-JAR distribution of the function (e.g. for use in the Java Lambda runtime):

```shell
../gradlew build
```

To build the native distribution of the function (e.g. for use in a custom Lambda runtime):

```shell
../gradlew nativeLambdaBuild
```

Note that building the native distribution requires Docker to be installed, and will make significant use of CPU and RAM.
The native distribution can provide significantly faster cold startup times.

## Deploying the native function

To save costs, the native build of the function should be deployed and given access to an existing S3 bucket.

### Creating the bucket

The S3 bucket can use mostly the standard settings. Some notes:

* "Block all public access" should be enabled, as users should only be able to access its objects through the function.
* Versioning can be left disabled as the function does not expose it.

### Creating the function

First, create the function by using the AWS console, with these options:

* Select "Author from scratch".
* Enter a descriptive function name: the specific name does not matter.
* For the runtime, choose "Amazon Linux 2023".
  * Amazon Linux 2 will not work, as it does not include the required version of the standard C libraries.
* For the architecture, choose the appropriate one based on the architecture of the binaries inside the `.zip` produced by the native lambda build.
  * This can be identified by using the `file` program on the `func` binary.
* In "Additional Configurations":
  * Tick the "Enable function URL" option.

### Upload the binaries

Scroll down to the "Code source" option, and upload the built ZIP file at `./build/libs/shorturl-s3-lambda-*-lambda.zip`.

After some time, the code editor should switch from the initial sample files, to two files:

* The `bootstrap` script (we will edit it manually).
* The `func` binary with the lambda code.

### Edit the bootstrap script

The bootstrap script needs to be edited to enable AWS, and indicate the bucket to be used.
This can be done with Java system properties (`-Dprop=value`).

Specifically we should set:

* `micronaut.object-storage.aws.default.enabled` to `true`.
* `micronaut.object-storage.aws.default.bucket` to the name of the S3 bucket we created.

The relevant command in the `bootstrap` script would look like this:

```shell
./func -Dmicronaut.object-storage.aws.default.enabled=true \
  -Dmicronaut.object-storage.aws.default.bucket=your-bucket \
  -Djava.library.path=/function
```

### Configure permissions

In the "Configuration" tab, go to the "Permissions" section.
Initially, the "Resource summary" will not list any permissions for S3.

Click on the link under "Role name" to visit the execution role associated to this lambda.
In "Permissions policies", use "Add permissions" to create an inline policy.

Select "S3", and add the "Get Object" and "Put Object" permissions.
Indicate in "Resources" that it should only be applicable to the ARN of the S3 bucket created above.

Save the changes. Check again in the "Resource summary" that the function has GetObject and PutObject rights on the S3 bucket.

### Configure the function URL

In the "Configuration" tab, go to the "Function URL" section and click on Edit:

* Auth type should be NONE
* "Configure cross-origin resource sharing" should be enabled:
  * Origin can be left as "*", or limited to specific domains
  * Expose headers should have:
    * content-type
    * access-control-allow-origin
    * access-control-allow-methods
  * Allow headers should have:
    * content-type
  * Allow methods should only have POST

### Test the function from inside AWS

Go to the "Test" tab, and try executing the function against this event:

```json
{
  "path": "/",
  "headers": {
    "Accept": "application/json"
  },
  "body": "{\"content\":\"hello world\"}"
}
```

This should return a response with a body consisting of a JSON document, with the ID to be used in the future:

```json
{
  "statusCode": 200,
  "headers": {
    "Content-Type": "application/json"
  },
  "body": "{\"shortened\":\"<your-ID>\"}"
}
```

You can then test using an event where the body has the ID, to see if you can retrieve the saved object:

```json
{
  "path": "/",
  "headers": {
    "Accept": "application/json"
  },
  "body": "{\"shortened\":\"<your-ID>\"}"
}
```

The request should return the original text that you had in the first request.

### Test from outside AWS with curl

Finally, you should test if the function URL is working as intended.
You can test saving a specific snippet with a command like this one, replacing the URL with the one provided in the AWS console:

```shell
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"content":"hello world"}' \
  https://ID.lambda-url.REGION.on.aws/
```

Note that the `Content-Type` header is very important: if it is not set, the AWS API Gateway will perform base-64 encoding on the body of the message, which will not be understood by the function.

This should produce an output like this:

```json
{"shortened":"<your-id>"}
```

You can send back that response to see if it can retrieve the original content:

```shell
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"shortened":"<your-id>"}' \
  https://ID.lambda-url.REGION.on.aws/
```

If this works, your function should be ready for use in the Epsilon Playground's Share button.