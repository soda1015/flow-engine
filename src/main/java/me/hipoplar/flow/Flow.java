package me.hipoplar.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "flow")
@XmlType(propOrder = { "name", "nodes", "paths", "businessId", "businessName", "status" })
public class Flow {
	public final static int FLOW_SATUS_INIT = 0;
	public final static int FLOW_SATUS_PROCESSING = 1;
	public final static int FLOW_SATUS_COMPLETE = 2;
	
	private String name;
	private List<Node> nodes;
	private List<Path> paths;
	private String businessId;
	private String businessName;
	private Integer status;
	@XmlTransient
	private FlowContext<?> context;

	@Override
	public String toString() {
		return "Flow - name: " + name;
	}
	
	public Flow() { super(); }
	
	public static Flow create(String flowName) {
		Flow flow = new Flow();
		flow.setName(flowName);
		flow.setStatus(Flow.FLOW_SATUS_INIT);
		return flow;
	}
	
	public Node addNode(String name, int type) {
		Node node = new Node(type, UUID.randomUUID().toString(), name, null);
		if(nodes == null) nodes = new ArrayList<>();
		nodes.add(node);
		return node;
	}
	
	public List<Path> addPaths(String expression, String from, String... tos) {
		Node n1 = search(from);
		n1.setExpression(expression);
		List<Path> pathList = new ArrayList<>();
		for (String to : tos) {
			Path path = new Path(from, to);
			pathList.add(path);
		}
		if(paths == null) paths = new ArrayList<>();
		paths.addAll(pathList);
		return pathList;
	}
	
	public Node search(String key) {
		if (nodes == null || nodes.size() == 0 || key == null) {
			return null;
		}
		for (Node node : nodes) {
			if(key.equals(node.getKey())) {
				return node;
			}
		}
		return null;
	}
	
	public List<Node> search(int type) {
		if (nodes == null || nodes.size() == 0) {
			return null;
		}
		List<Node> result = new ArrayList<>();
		for (Node node : nodes) {
			if(type == node.getType()) {
				result.add(node);
			}
		}
		return result.isEmpty() ? null : result;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public FlowContext<?> getContext() {
		return context;
	}

	public void setContext(FlowContext<?> context) {
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
