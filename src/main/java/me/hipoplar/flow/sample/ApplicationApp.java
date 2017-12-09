package me.hipoplar.flow.sample;

import java.util.List;
import java.util.UUID;

import me.hipoplar.flow.Activity;
import me.hipoplar.flow.Expression;
import me.hipoplar.flow.Flow;
import me.hipoplar.flow.FlowContext;
import me.hipoplar.flow.FlowEngine;
import me.hipoplar.flow.Node;
import me.hipoplar.flow.Operator;
import me.hipoplar.flow.Path;

public class ApplicationApp {
	public static void main(String[] args) {
		System.out.println("Defining flow...");
		Flow flow = new Flow();

		flow.setName("Application Flow");

		String operatorId = "1001";
		String operatorName = "Vincent";
		String operatorGroup = "ANY";
		// Start node
		Node start = flow.addNode("Start", Node.NODE_TYPE_START);
		start.addOperator(operatorId, operatorName, operatorGroup);
		// Application node
		Node application = flow.addNode("Apply", Node.NODE_TYPE_TASK);
		application.addOperator(operatorId, operatorName, operatorGroup);
		// Verification node
		Node verification = flow.addNode("Verify", Node.NODE_TYPE_GATEWAY);
		verification.addOperator(operatorId, operatorName, operatorGroup);
		// End node
		Node end = flow.addNode("End", Node.NODE_TYPE_END);
		end.addOperator(operatorId, operatorName, operatorGroup);
		// Start to Application
		flow.addPaths(new Expression(application.getKey()).build(), start.getKey(), application.getKey());
		// Application to Verification
		Expression applyExpression = new Expression().iF("context.applied").then(verification.getKey()).elseThen(application.getKey());
		flow.addPaths(applyExpression.build(), application.getKey(), verification.getKey());
		// Verification to End and Verification back to Application
		Expression verifyExpression = new Expression().iF("context.verified").then(end.getKey()).elseThen(application.getKey());
		flow.addPaths(verifyExpression.build(), verification.getKey(), application.getKey(), end.getKey());
		// Create engine
		FlowEngine flowEngine = FlowEngine.createEngine();
		// Create flow
		flowEngine.createFLow(flow);
		Flow result = flowEngine.getFlow(flow.getName());
		System.out.println(result);
		for (Node node : result.getNodes()) {
			System.out.println(node);
		}

		for (Path path : result.getPaths()) {
			System.out.println(path);
		}
		System.out.println("====================================================================================");
		// Prepare application form
		System.out.println("Preparing application form...");
		FlowContext<Application> context = new FlowContext<>();
		Application sample = new Application();
		sample.setApplied(true);
		sample.setId(UUID.randomUUID().toString());
		sample.setMobile("18600000000");
		sample.setName("Lisa");
		sample.setVerified(false);
		context.setData(sample);
		Operator operator = new Operator();
		operator.setGroup(operatorGroup);
		operator.setOperatorId(operatorId);
		operator.setOperatorName(operatorName);
		context.setOperator(operator);
		context.setOperator(operator);
		// Start flow
		System.out.println("Starting flow...");
		flowEngine.start(flow.getName(), context);
		// Submit
		System.out.println("Submiting form...");
		List<Activity> activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			flowEngine.process(activity.getId(), context);
		}
		// Reject
		System.out.println("Rejecting application...");
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			sample.setVerified(false);
			flowEngine.process(activity.getId(), context);
		}
		System.out.println("Opps! Your application was rejected! Please resubmit it again!");
		// Resubmit
		System.out.println("Resubmiting form...");
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			sample.setName("Jobs");
			flowEngine.process(activity.getId(), context);
		}
		// Verify
		System.out.println("Verifying...");
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			sample.setVerified(true);
			flowEngine.process(activity.getId(), context);
		}

		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		if (activities == null || activities.isEmpty()) {
			System.out.println("Welcome to join us, enjoy it!");
		}
	}
}