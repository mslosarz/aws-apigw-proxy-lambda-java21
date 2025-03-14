#!/bin/bash

SOLUTION_NAME=$1
TIMESTAMP=$(date +%Y%m%d%H%M%S)

wait_stack_creation_completed() {
  STACK_NAME=$1
  echo "Waiting for $STACK_NAME to be completed"
  STATUS=$(aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME")
  if [[ ${STATUS} -ne 0 ]]; then

    echo "Creation has failed $STATUS"
    exit ${STATUS}
  fi
}

wait_stack_update_completed() {
  STACK_NAME=$1
  echo "Waiting for $STACK_NAME to be completed"
  STATUS=$(aws cloudformation wait stack-update-complete --stack-name "$STACK_NAME")
  if [[ ${STATUS} -ne 0 ]]; then

    echo "Creation has failed $STATUS"
    exit ${STATUS}
  fi
}

build_app() {
  S3_BUCKET=$1
  mvn clean package
  aws s3 cp target/lambda.jar s3://${S3_BUCKET}/lambda-${TIMESTAMP}.jar
}

create_s3() {
  CURRENT_SOLUTION_NAME="$SOLUTION_NAME-s3"
  STACK_EXISTS=$(aws cloudformation describe-stacks --stack-name "$CURRENT_SOLUTION_NAME" || echo "create")
  if [ "$STACK_EXISTS" == "create" ]; then
    aws cloudformation create-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/s3-lambda-storage.cfn.yaml --parameters ParameterKey=SolutionName,ParameterValue=$CURRENT_SOLUTION_NAME
    wait_stack_creation_completed "$CURRENT_SOLUTION_NAME"
  else
    WAIT_FOR_UPDATE=$(aws cloudformation update-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/s3-lambda-storage.cfn.yaml --parameters ParameterKey=SolutionName,UsePreviousValue=true || echo "no updates")
    if [ "$WAIT_FOR_UPDATE" != "no updates" ]; then
      wait_stack_update_completed "$CURRENT_SOLUTION_NAME"
    fi
  fi
}

create_api_gw_with_lambda() {
  S3_BUCKET=$1
  CURRENT_SOLUTION_NAME="$SOLUTION_NAME-apigw-with-lambda"
  STACK_EXISTS=$(aws cloudformation describe-stacks --stack-name "$CURRENT_SOLUTION_NAME" || echo "create")
  if [ "$STACK_EXISTS" == "create" ]; then
    aws cloudformation create-stack --stack-name "$CURRENT_SOLUTION_NAME" \
        --template-body file://cfn/apigw-lambda-proxy.cfn.yaml \
        --parameters ParameterKey=SolutionName,ParameterValue=$CURRENT_SOLUTION_NAME ParameterKey=S3Bucket,ParameterValue=$S3_BUCKET ParameterKey=ArtifactName,ParameterValue=lambda-${TIMESTAMP}.jar \
        --capabilities CAPABILITY_IAM
    wait_stack_creation_completed "$CURRENT_SOLUTION_NAME"
  else
    WAIT_FOR_UPDATE=$(aws cloudformation update-stack --stack-name "$CURRENT_SOLUTION_NAME" \
                          --template-body file://cfn/apigw-lambda-proxy.cfn.yaml \
                          --parameters ParameterKey=SolutionName,UsePreviousValue=true ParameterKey=S3Bucket,UsePreviousValue=true ParameterKey=ArtifactName,ParameterValue=lambda-${TIMESTAMP}.jar \
                          --capabilities CAPABILITY_IAM || echo "no updates")
    if [ "$WAIT_FOR_UPDATE" != "no updates" ]; then
      wait_stack_update_completed "$CURRENT_SOLUTION_NAME"
    fi
  fi
}

create_s3
S3_BUCKET=$(aws cloudformation describe-stacks --stack-name "$SOLUTION_NAME-s3" --output text  --query 'Stacks[0].Outputs[?OutputKey==`S3Bucket`].OutputValue')
build_app "$S3_BUCKET"
create_api_gw_with_lambda "$S3_BUCKET"
SITE_URL=$(aws cloudformation describe-stacks --stack-name "$SOLUTION_NAME-apigw-with-lambda" --output text  --query 'Stacks[0].Outputs[?OutputKey==`ApiGwFacade`].OutputValue')
echo "============================================"
echo "Visit: $SITE_URL"
echo "============================================"

