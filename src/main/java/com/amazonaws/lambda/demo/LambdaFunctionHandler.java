package com.amazonaws.lambda.demo;

import java.io.UnsupportedEncodingException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.CopyObjectResult;

import java.net.URLDecoder;


public class LambdaFunctionHandler implements RequestHandler<S3EventNotification, String> {

	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	/*
	 * Required input parameter keys.
	 */

	private static final String INPUT_KEY_DEST_BUCKET = "myfirstawsbucketgspann";

	public LambdaFunctionHandler() {
	}

	// Test purpose only.
	LambdaFunctionHandler(AmazonS3 s3) {
		this.s3 = s3;
	}

	@Override
	public String handleRequest(S3EventNotification event, Context context) {
		context.getLogger().log("Received event: " + event);
		context.getLogger().log("Logging the Events:-"+event.toJson());
		
		context.getLogger().log("Logging the Events:-"+event.getRecords().size());

		String sourceBucketName = event.getRecords().get(0).getS3().getBucket().getName();
		String fileKeyName = event.getRecords().get(0).getS3().getObject().getKey();
		// Get the object from the event and show its content type

		try {
			CopyObjectResult result = s3.copyObject(sourceBucketName, fileKeyName, INPUT_KEY_DEST_BUCKET,
					fileKeyName);
			return result.getETag();
		} catch (Exception e) {
			e.printStackTrace();
			context.getLogger()
					.log(String.format(
							"Error getting object %s from bucket %s. Make sure they exist and"
									+ " your bucket is in the same region as this function.",
							fileKeyName, sourceBucketName));
			throw e;
		}
	}
	
	public static String decodeURIComponent(String s, String charset) {
        if (s == null) {
            return null;
        }

        String result = null;

        try {
            result = URLDecoder.decode(s, charset);
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
	
	  public static String decodeURIComponent(String s) {
	        return decodeURIComponent(s, "UTF-8");
	    }

}