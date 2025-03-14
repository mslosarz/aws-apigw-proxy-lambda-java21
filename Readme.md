# AWS API Gateway with Lambda (Java 21 + Dagger)

This project sets up an AWS API Gateway integrated with an AWS Lambda function. The Lambda function is built using Java 21 and leverages the Dagger framework for Dependency Injection.

## Project Structure

Lambda Function: Built with Maven (mvn), using Java 21 and Dagger for DI.

- **API Gateway**: Configured to trigger the Lambda function.
- **Infrastructure**: Built using AWS CloudFormation. The CloudFormation templates are located in the `cfn` folder.
- **Deployment Scripts:**
  - `./deploy <solution_name>`: Deploys the API Gateway and Lambda function.
  - `./undeploy <solution_name>`: Removes the deployed resources.

## Prerequisites

Ensure you have the following installed and configured:

- AWS CLI configured with appropriate credentials
- AWS SAM CLI (optional but recommended for testing)
- Java 21
- Maven
- Bash (for running deployment scripts)

## Building the Lambda Function

Run the following command to build the project:

`mvn clean package`

This will generate a deployment-ready JAR file in the target directory.

## Deployment

To deploy the solution, run:

`./deploy <solution_name>`

This script will:
- Package and upload the Lambda function.
- Create an API Gateway endpoint.
- Configure the necessary permissions.

## Undeployment

To remove the deployed resources, run:

`./undeploy <solution_name>`

This script will:

- Delete the API Gateway configuration.
- Remove the Lambda function.
- Clean up any associated resources.

## Testing the API

After deployment, you can test the API using curl:

`curl -X GET https://<api_gateway_url>/<resource>`

Replace <api_gateway_url> with the actual endpoint provided after deployment.

## Notes

The Dagger framework is used for Dependency Injection within the Lambda function to manage components efficiently.
The deploy and undeploy scripts assume proper AWS permissions are in place.

