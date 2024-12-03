import {define} from "ace-builds";

define("ace/mode/flexmi_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/xml_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
//var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;
var XmlHighlightRules = require("./xml_highlight_rules").XmlHighlightRules;


var flexmiHighlightRules = function() {

    //Use the ace highligting rules for xml
    this.$rules = new XmlHighlightRules().getRules();
}

oop.inherits(flexmiHighlightRules , XmlHighlightRules);

exports.flexmiHighlightRules = flexmiHighlightRules;
});


define("ace/mode/flexmi",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/flexmi_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var flexmiHighlightRules = require("./flexmi_highlight_rules").flexmiHighlightRules;

var Mode = function() {
    this.HighlightRules = flexmiHighlightRules;
    this.$behaviour = this.$defaultBehaviour;
};
oop.inherits(Mode, TextMode);

(function() {
    this.$id = "ace/mode/flexmi";
    this.snippetFileId = "ace/snippets/flexmi";
}).call(Mode.prototype);

exports.Mode = Mode;

});
