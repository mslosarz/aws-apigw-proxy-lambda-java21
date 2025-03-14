#!/bin/bash

SOLUTION_NAME=$1

delete_stack_and_wait() {
  STACK=$1
  aws cloudformation delete-stack --stack-name "$STACK"
  echo "Waiting for $STACK to be removed"
  STATUS=$(aws cloudformation wait stack-delete-complete --stack-name "$STACK")
  echo "Stack $STACK removed with status ${STATUS}"
}

S3_BUCKET=$(aws cloudformation describe-stacks --stack-name "$SOLUTION_NAME-s3" --output text  --query 'Stacks[0].Outputs[?OutputKey==`S3Bucket`].OutputValue')
echo "Cleaning... ${S3_BUCKET}"

aws s3api list-object-versions --bucket "${S3_BUCKET}" \
     --output json --query 'Versions[].[Key, VersionId]' \
     | jq -r '.[] | "--key '\''" + .[0] + "'\'' --version-id " + .[1]' \
     | xargs -L1 aws s3api delete-object --bucket "${S3_BUCKET}"

delete_stack_and_wait "$SOLUTION_NAME-apigw-with-lambda"
delete_stack_and_wait "$SOLUTION_NAME-s3"

