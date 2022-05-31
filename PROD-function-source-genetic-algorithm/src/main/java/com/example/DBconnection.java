package com.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DBconnection {
	public static MongoClient client = MongoClients.create(
			"mongodb+srv://gabe123:gabe123@cluster0.txvts.mongodb.net/test?retryWrites=true&w=majority");
	public static MongoDatabase db = client.getDatabase("test");

	// get tables
	public static MongoCollection<Document> db_instructors = db.getCollection("instructors");
	public static MongoCollection<Document> db_courses = db.getCollection("courses");
	public static MongoCollection<Document> db_schedule = db.getCollection("schedules");
	public static MongoCollection<Document> db_timeslots = db.getCollection("timeslots");

}
