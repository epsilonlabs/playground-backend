// We match persons by name
rule NodeWithNode
    match l : Left!Node
    with r : Right!Node {

    compare: l.label = r.label
}