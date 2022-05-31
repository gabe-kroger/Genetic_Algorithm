package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;

public class TimetableGA {
    // implements HttpFunction {

    // public void service(HttpRequest request, HttpResponse response) throws
    // Exception {
    public static void main(String[] args) {

        // BufferedWriter writer = response.getWriter();
        // Get a Timetable object with all the available information.
        Timetable timetable = initializeTimetable();

        // Initialize GA
        GeneticAlgorithm ga = new GeneticAlgorithm(500, 0.03, 1.1, 3, 5);

        // Initialize population
        Population population = ga.initPopulation(timetable);

        // Evaluate population
        ga.evalPopulation(population, timetable);

        // Keep track of current generation
        int generation = 1;

        // Start evolution loop
        while (ga.isTerminationConditionMet(generation, 1000) == false
                && ga.isTerminationConditionMet(population) == false) {
            // while (ga.isTerminationConditionMet(population) == false) {
            // Print fitness
            // writer.write("G" + generation + " Best fitness: " +
            // population.getFittest(0).getFitness() + "\n");
            System.out.println("G" + generation + " Best fitness: " + population.getFittest(0).getFitness() + "\n");

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population, timetable);

            // Evaluate population
            ga.evalPopulation(population, timetable);

            // Increment the current generation
            generation++;
        }

        // Print fitness
        timetable.createClasses(population.getFittest(0));
        System.out.println("\n");
        System.out.println("Solution found in " + generation + " generations");
        System.out.println("Final solution fitness: " + population.getFittest(0).getFitness());
        System.out.println("Clashes: " + timetable.calcClashes(timetable));

        DBconnection.db_schedule.deleteMany(new Document());
        Gson gson = new Gson();
        // Print classes

        Class classes[] = timetable.getClasses();
        int classIndex = 1;
        for (int i = 0; i < classes.length; i++) {

            timetable.getProfessor(classes[i].getProfessorId()).called();

            Schedule s1 = new Schedule(classIndex,
                    timetable.getModule(classes[i].getModuleId()).getModuleCode(),
                    timetable.getModule(classes[i].getModuleId()).getModuleName(),
                    timetable.getProfessor(classes[i].getProfessorId()).getProfessorName(),
                    timetable.getTimeslot(classes[i].getTimeslotId()).getTimeslot());

            String json = gson.toJson(s1);
            Document doc = Document.parse(json);
            DBconnection.db_schedule.insertOne(doc);

            classIndex++;
        }

        Professor[] professor = timetable.getProfessorsAsArray();
        for (int i = 0; i < professor.length; i++) {

            Document query = new Document("lastName", professor[i].getProfessorName());
            Bson updates = Updates.set("assignedClasses", professor[i].getNumAssigned());
            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = DBconnection.db_instructors.updateOne(query, updates,
                    options);

        }

        Rstatus nStatus = new Rstatus(1, "success");

    }

    /**
     * Creates a Timetable with all the necessary course information.
     * 
     * @return timetable
     */
    private static Timetable initializeTimetable() {

        // Init database connections

        Gson gson = new Gson();

        // convert all instructors in database to java objects
        FindIterable<Document> fItr1 = DBconnection.db_instructors.find();
        MongoCursor<Document> instructorCursor = fItr1.iterator();
        int numInstructors = (int) DBconnection.db_instructors.countDocuments();
        Professor[] pInstructors = new Professor[numInstructors];
        for (int i = 0; i < numInstructors; i++) {
            Document doc2 = instructorCursor.next();
            // Instructor tempInstructor = gson.fromJson(doc2.toJson(), Instructor.class);
            pInstructors[i] = (gson.fromJson(doc2.toJson(), Professor.class));
            pInstructors[i].setJavaId(i + 1);
            pInstructors[i].setAssigned(0);
            // pInstructors[i].print();
        }
        instructorCursor.close();

        // convert all courses in database to java objects
        FindIterable<Document> fItr2 = DBconnection.db_courses.find();
        MongoCursor<Document> couseCursor = fItr2.iterator();

        int numCourses = (int) DBconnection.db_courses.countDocuments();
        Course[] pCourses = new Course[numCourses];
        for (int i = 0; i < numCourses; i++) {
            Document doc3 = couseCursor.next();
            // Instructor tempInstructor = gson.fromJson(doc2.toJson(), Instructor.class);
            pCourses[i] = (gson.fromJson(doc3.toJson(), Course.class));
            pCourses[i].setJavaId(i);
            // tempInstructor.print();
            // pCourses[i].print();
        }
        couseCursor.close();
        // Create timetable
        Timetable timetable = new Timetable();

        // Set up rooms
        timetable.addRoom(1, "A1", 100);
        timetable.addRoom(2, "B1", 100);
        timetable.addRoom(3, "c1", 100);
        timetable.addRoom(4, "d1", 100);
        timetable.addRoom(5, "e1", 100);
        timetable.addRoom(6, "f1", 100);
        timetable.addRoom(7, "g1", 100);
        timetable.addRoom(8, "h1", 100);

        // Set up timeslots
        Timeslot[] ts = new Timeslot[16];
        ts[0] = new Timeslot(1, "T,TR: 9:25 - 10:40", null, new int[] { 925, 1040 }, null, new int[] { 925, 1040 },
                null);
        ts[1] = new Timeslot(2, "M,W: 12:15 - 13:30", new int[] { 925, 1040 }, null, new int[] { 925, 1040 }, null,
                null);
        ts[2] = new Timeslot(3, "T,Th: 17:55 - 19:10", null, new int[] { 1755, 1910 }, null, new int[] { 1755, 1910 },
                null);
        ts[3] = new Timeslot(4, "M,W,F: 9:25 - 10:15", new int[] { 925, 1015 }, null, new int[] { 925, 1015 }, null,
                new int[] { 925, 1015 });
        ts[4] = new Timeslot(5, "M,F,W: 12:15 - 13:05", new int[] { 1215, 1305 }, null, new int[] { 1215, 1305 }, null,
                new int[] { 1215, 1305 });
        ts[5] = new Timeslot(6, "T,TR: 8:00 - 9:15", null, new int[] { 800, 915 }, null, new int[] { 900, 915 }, null);
        ts[6] = new Timeslot(7, "M,W: 8:00 - 9:15", new int[] { 800, 915 }, null, new int[] { 800, 915 }, null, null);
        ts[7] = new Timeslot(8, "M,W: 9:25 - 10:40", new int[] { 925, 1040 }, null, new int[] { 925, 1040 }, null,
                null);
        ts[8] = new Timeslot(9, "T, TR: 12:15 - 13:30", null, new int[] { 1215, 1330 }, null, new int[] { 1215, 1330 },
                null);
        ts[9] = new Timeslot(10, "M,W: 10:50 - 11:55", new int[] { 1050, 1155 }, null, new int[] { 1050, 1155 }, null,
                null);
        ts[10] = new Timeslot(11, "T,TR: 10:50 - 11:55", null, new int[] { 1050, 1155 }, null, new int[] { 1050, 1155 },
                null);
        ts[11] = new Timeslot(12, "T,TR: 10:55 - 11:55", null, new int[] { 1055, 1155 }, null, new int[] { 1055, 1155 },
                null);
        ts[12] = new Timeslot(13, "M,W,F: 10:50 - 11:40", new int[] { 1050, 1140 }, null, new int[] { 1050, 1140 },
                null, new int[] { 1050, 1140 });
        ts[13] = new Timeslot(14, "M,W: 10:55 - 11:55", new int[] { 1055, 1155 }, null, new int[] { 1055, 1155 }, null,
                null);
        ts[14] = new Timeslot(15, "M,W,F: 8:00 - 8:50", new int[] { 800, 850 }, null, new int[] { 800, 850 }, null,
                new int[] { 800, 850 });
        ts[15] = new Timeslot(16, "M,W,F: 16:30 - 17:20", new int[] { 1630, 1720 }, null, new int[] { 1630, 1720 },
                null,
                new int[] { 1630, 1720 });

        checkOverlaps(ts);
        removeId(ts);

        DBconnection.db_timeslots.deleteMany(new Document());
        // set up timeslots
        for (Timeslot ts1 : ts) {
            System.out.print(ts1.getTimeslotId() + "  " + ts1.getTimeslot() + "   ");
            ts1.printAvoid();
            String json = gson.toJson(ts1);
            Document doc = Document.parse(json);
            DBconnection.db_timeslots.insertOne(doc);
            timetable.addTimeslot(ts1);
        }

        // Set up professors
        for (int i = 0; i < numInstructors; i++) {
            timetable.addProfessor(pInstructors[i]);
            // pInstructors[i].print();
        }

        Module[] modules = new Module[numCourses];
        for (int i = 0; i < numCourses; i++) {
            modules[i] = new Module(i + 1, pCourses[i].courseNumber, pCourses[i].courseTitle,
                    findOverlap(pCourses[i], pInstructors));
            timetable.addModule(modules[i]);
            // modules[i].print();
            // //writer.write(String.format("TIMETABLE:: id: %d, crn: %s, title: %s,\t\t
            // dis: %s \n", i+1, pCourses[i].courseNumber, pCourses[i].getCourseTitle(),
            // pCourses[i].dString()));
        }

        // Set up student groups and the modules they take.
        // timetable.addGroup(1, 10, timetable.getModuleIds());
        timetable.addGroup(1, 1, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 });
        timetable.addGroup(2, 2, new int[] { 2, 5, 8, 12, 13, 15, 17, 19 });
        timetable.addGroup(3, 1, new int[] { 2, 19 });

        return timetable;
    }

    /**
     * find instructor who can teach a give course based on the disciplines
     * 
     * @param course
     * @param instructors
     * @return integer array of professor id's
     */
    public static int[] findOverlap(Course course, Professor[] instructors) {
        int idx = 0;
        int[] large = new int[100];
        for (int i = 0; i < course.getDisciplines().length; i++) {
            for (int j = 0; j < instructors.length; j++) {
                for (int k = 0; k < instructors[j].getDisciplines().length; k++) {
                    if (course.getDisciplines()[i].equals(instructors[j].getDisciplines()[k])) {
                        large[idx] = instructors[j].getProfessorId();
                        idx++;
                    }
                }
            }
        }
        int[] smaller = Arrays.copyOf(large, idx);
        Arrays.sort(smaller);
        return removeDups(smaller);
    }

    public static void checkOverlaps(Timeslot[] ts) {

        for (Timeslot ts1 : ts) {
            // check for m
            if (ts1.m != null) {
                for (int i = 0; i < ts1.m.length; i = i + 2) {
                    int ts1Start = ts1.m[i];
                    int ts1End = ts1.m[i + 1];
                    for (Timeslot ts2 : ts) {
                        if (ts2.m != null) {
                            for (int j = 0; j < ts2.m.length; j = j + 2) {
                                String st = "";
                                int ts2Start = ts2.m[i];
                                int ts2End = ts2.m[i + 1];
                                if ((ts2Start <= ts1Start && ts1Start < ts2End)
                                        || (ts1Start <= ts2Start && ts2Start < ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "monday case 1";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                } else if ((ts2Start < ts1End && ts1End <= ts2End)
                                        || (ts1Start < ts2End && ts2End <= ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "monday case 2";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                }
                            }
                        }
                    }
                }
            }

            // check for t
            if (ts1.t != null) {
                for (int i = 0; i < ts1.t.length; i = i + 2) {
                    int ts1Start = ts1.t[i];
                    int ts1End = ts1.t[i + 1];
                    for (Timeslot ts2 : ts) {
                        if (ts2.t != null) {
                            for (int j = 0; j < ts2.t.length; j = j + 2) {
                                String st = "";
                                int ts2Start = ts2.t[i];
                                int ts2End = ts2.t[i + 1];
                                if ((ts2Start <= ts1Start && ts1Start < ts2End)
                                        || (ts1Start <= ts2Start && ts2Start < ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "tuesday case 1";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                } else if ((ts2Start < ts1End && ts1End <= ts2End)
                                        || (ts1Start < ts2End && ts2End <= ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "tuesday case 2";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                }
                            }
                        }
                    }
                }

            }

            // check for w
            if (ts1.w != null) {
                for (int i = 0; i < ts1.w.length; i = i + 2) {
                    int ts1Start = ts1.w[i];
                    int ts1End = ts1.w[i + 1];
                    for (Timeslot ts2 : ts) {
                        if (ts2.w != null) {
                            for (int j = 0; j < ts2.w.length; j = j + 2) {
                                String st = "";
                                int ts2Start = ts2.w[i];
                                int ts2End = ts2.w[i + 1];
                                if ((ts2Start <= ts1Start && ts1Start < ts2End)
                                        || (ts1Start <= ts2Start && ts2Start < ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "wednesday case 1";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                } else if ((ts2Start < ts1End && ts1End <= ts2End)
                                        || (ts1Start < ts2End && ts2End <= ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "wednesday case 2";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                }
                            }
                        }
                    }
                }
            }

            // check for th
            if (ts1.th != null) {
                for (int i = 0; i < ts1.th.length; i = i + 2) {
                    int ts1Start = ts1.th[i];
                    int ts1End = ts1.th[i + 1];
                    for (Timeslot ts2 : ts) {
                        if (ts2.th != null) {
                            for (int j = 0; j < ts2.th.length; j = j + 2) {
                                String st = "";
                                int ts2Start = ts2.th[i];
                                int ts2End = ts2.th[i + 1];
                                if ((ts2Start <= ts1Start && ts1Start < ts2End)
                                        || (ts1Start <= ts2Start && ts2Start < ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "Thursday case 1";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                } else if ((ts2Start < ts1End && ts1End <= ts2End)
                                        || (ts1Start < ts2End && ts2End <= ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "Thursday case 2";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                }
                            }
                        }
                    }
                }
            }

            // check for f
            if (ts1.f != null) {
                for (int i = 0; i < ts1.f.length; i = i + 2) {
                    int ts1Start = ts1.f[i];
                    int ts1End = ts1.f[i + 1];
                    for (Timeslot ts2 : ts) {
                        if (ts2.f != null) {
                            for (int j = 0; j < ts2.f.length; j = j + 2) {
                                String st = "";
                                int ts2Start = ts2.f[i];
                                int ts2End = ts2.f[i + 1];
                                if ((ts2Start <= ts1Start && ts1Start < ts2End)
                                        || (ts1Start <= ts2Start && ts2Start < ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "friday case 1";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                } else if ((ts2Start < ts1End && ts1End <= ts2End)
                                        || (ts1Start < ts2End && ts2End <= ts1End)) {
                                    ts1.addAvoid(ts2.getTimeslotId());
                                    st += "friday case 2";
                                    System.out.println(String.format(
                                            "id1: %d. ts1S: %d, ts1E: %d, id2: %d, ts2S: %d, ts2E: %d %s",
                                            ts1.getTimeslotId(), ts1Start, ts1End, ts2.getTimeslotId(), ts2Start,
                                            ts2End,
                                            st));
                                }
                            }
                        }
                    }
                }
            }
            ts1.avoid = removeDups(ts1.avoid);

        }
    }

    public static void removeId(Timeslot[] ts) {
        for (Timeslot ts1 : ts) {
            int toRemove = ts1.getTimeslotId();
            if (ts1.avoid.length == 1) {
                ts1.avoid = null;
            } else {
                int[] temp = new int[ts1.avoid.length - 1];
                int tempIdx = 0;
                for (int i = 0; i < ts1.avoid.length; i++) {
                    if (ts1.avoid[i] != toRemove) {
                        temp[tempIdx] = ts1.avoid[i];
                        tempIdx++;
                    }
                }
                ts1.avoid = temp;
            }
        }
    }

    /**
     * Remove all id's which are duplicates and 0
     * 
     * @param og
     * 
     * @return cleanArray
     */
    public static int[] removeDups(int[] a) {
        // Hash map which will store the
        // elements which has appeared previously.
        HashMap<Integer, Boolean> mp = new HashMap<Integer, Boolean>();
        int n = a.length;
        int[] temp = new int[n];
        int idx = 0;

        for (int i = 0; i < n; ++i) {

            // Print the element if it is not
            // present there in the hash map
            // and Insert the element in the hash map
            if (mp.get(a[i]) == null) {
                temp[idx] = a[i];
                idx++;
                mp.put(a[i], true);
            }
        }
        Arrays.sort(temp);
        int numZero = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == 0) {
                numZero++;
            }
        }
        int[] fin = new int[temp.length - numZero];
        for (int i = 0; i < fin.length; i++) {
            fin[i] = temp[i + numZero];
        }

        return fin;
    }

}
