package com.mkyong.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

@Controller
@RequestMapping("/movie")
public class MovieController {
	
	static AmazonDynamoDBClient dynamoDB;
	static final Logger logger = LogManager.getLogger(MovieController.class.getName());
	@RequestMapping(value="/{name}", method = RequestMethod.GET)
	public String getMovie(@PathVariable String name, ModelMap model) throws Exception {
		init();

		try {
			String tableName = "poc_Ride";
			logger.warn("--------------------------------------------------------------------");
			      

			// Scan items for movies with a year attribute greater than 1985
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withN("1"));

			scanFilter.put("Ride_ID", condition);
			ScanRequest scanRequest = new ScanRequest(tableName)/*.withScanFilter(scanFilter)*/;
			ScanResult scanResult = dynamoDB.scan(scanRequest);
			logger.warn("Result: " + scanResult);
			System.out.println("Count: " + scanResult.getCount());
			for(Map mapResult:scanResult.getItems()) {
				System.out.println(mapResult.entrySet());
				for(Object skey:mapResult.entrySet()) {
					System.out.println(skey+"-"+mapResult.get(skey));
				}
			}
			model.addAttribute("movie", name);
			model.addAttribute("result", scanResult);
			logger.warn("--------------------------------------------------------------------");
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		//model.addAttribute("movie", name);
		return "list";

	}
	private void init() throws Exception {

	//	AWSCredentials credentials = null;
		try {
			//credentials = new ProfileCredentialsProvider("default").getCredentials();
			
		      //  Construct an AssumeRoleRequest object using the provided role ARN and role session name.
		      /* AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest().withRoleArn("arn:aws:iam::673924706978:role/aws-elasticbeanstalk-service-role");
		       AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient();
		       //  Submit the requesting the assumeRole method of the stsClient object. 
		       AssumeRoleResult assumeRoleResult = stsClient.assumeRole(assumeRoleRequest);
		       //  Return the credentials from the request result.
		       Credentials   stsCredentials = assumeRoleResult.getCredentials();*/
		      
			
			
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\Administrator\\.aws\\credentials), and is in valid format.",
							e);
		}
	//	dynamoDB = new AmazonDynamoDBClient(credentials);
		AWSCredentialsProvider provider = new InstanceProfileCredentialsProvider();
        dynamoDB = new AmazonDynamoDBClient(provider);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		dynamoDB.setRegion(usEast1);
	}
}