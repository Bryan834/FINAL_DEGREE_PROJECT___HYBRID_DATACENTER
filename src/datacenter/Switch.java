package datacenter;

public class Switch extends Node {
    private NodeType type;

    public Switch(String id) {
        super(id);
        this.type = NodeType.Switch;
    }

    public NodeType getType() {
        return type;
    }
}