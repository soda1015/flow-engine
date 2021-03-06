package me.hipoplar.flow;

import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;
import me.hipoplar.flow.api.ActivityService;
import me.hipoplar.flow.api.DatabaseEngine;
import me.hipoplar.flow.api.FlowService;
import me.hipoplar.flow.api.GatewayService;
import me.hipoplar.flow.model.Activity;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.Node;
import me.hipoplar.flow.model.Operator;
import me.hipoplar.flow.simple.SimpleActivityService;
import me.hipoplar.flow.simple.SimpleDataBaseEngine;
import me.hipoplar.flow.simple.SimpleFlowService;
import me.hipoplar.flow.simple.SimpleGatewayService;

public class ApplicationTest extends TestCase {
	public void test() {
		/* =====================================================================================================*/
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
		Node verification = flow.addNode("Verify", Node.NODE_TYPE_TASK);
		verification.addOperator(operatorId, operatorName, operatorGroup);
		// Verification gateway
		Node gateway = flow.addNode("Verification Gateway", Node.NODE_TYPE_GATEWAY_EXCLUSIVE);
		gateway.addOperator(operatorId, operatorName, operatorGroup);
		// End node
		Node end = flow.addNode("End", Node.NODE_TYPE_END);
		end.addOperator(operatorId, operatorName, operatorGroup);
		// From Start to Application
		flow.direct(start.getKey(), application.getKey());
		// From Application to Verification
		flow.direct(application.getKey(), verification.getKey());
		// From Verification to Gateway
		flow.direct(verification.getKey(), gateway.getKey());
		// Gateway back to Application or to End
		Expression verifyExpression = new Expression().iF("context.verified").then(end.getKey()).elseThen(application.getKey());
		flow.exclude(verifyExpression.build(), gateway.getKey(), application.getKey(), end.getKey());
		/* =====================================================================================================*/
		
		// Create engine
		DatabaseEngine databaseEngine = new SimpleDataBaseEngine();
		FlowService flowService = new SimpleFlowService(databaseEngine);
		ActivityService activityService = new SimpleActivityService(databaseEngine);
		GatewayService gatewayService = new SimpleGatewayService(databaseEngine);
		FlowEngine flowEngine = new FlowEngine(flowService, activityService, gatewayService);
		// Create flow
		Flow definition = flowEngine.define(flow);
		Flow instance = flowEngine.instance(definition.getKey(), "A Application Sample");
		String flowxml = flowService.getFlow(instance.getKey()).getFlowxml();
		System.out.println("Flow: \n" + flowxml);
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
		flowEngine.start(flow.getKey(), context);
		// Submit
		System.out.println("Submiting form...");
		List<Activity> activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			flowEngine.process(activity.getId(), context);
		}
		// Reject
		System.out.println("Rejecting application...");
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			sample.setVerified(false);
			flowEngine.process(activity.getId(), context);
		}
		System.out.println("Opps! Your application was rejected! Please resubmit it again!");
		// Resubmit
		System.out.println("Resubmiting form...");
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			sample.setName("Jobs");
			flowEngine.process(activity.getId(), context);
		}
		// Verify
		System.out.println("Verifying...");
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
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