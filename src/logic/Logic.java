package logic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import logic.storage.Storage;


public class Logic {
	static String[] temp=new String[8];
	private String[] parameterToken = null;

	public static ArrayList<Task> taskList = null;
	public static int totalNumberOfTasks =  Storage.getMaxNumberOfTasks();

	public static ArrayList<Task> initTaskFromXML(){
		taskList = Storage.XmltoTable(Constants.XML_FILE_PATH);
		return taskList;
	}
	/*
    public static String add(String parameters) throws IOException {

    	String[] inputArray = parameters.split("-");

    	LogicAddTask.add(inputArray);

    	Storage.setMaxNumberOfTasks(Storage.getMaxNumberOfTasks() + 1);
    	Storage.tableToXml(Constants.XML_FILE_PATH, taskList);


//    	FileWriter fw = new FileWriter(Constants.FILENAME, true);
//    	BufferedWriter bw = new BufferedWriter(fw);
//    	
//    	bw.write(parameters + "\n");
//    	bw.close();
//    	
    	return "[ADD]Parameters entered are: " + parameters;
    }*/

	/*
	 * @param is correct 
	 * add meeting -10/03/2015 -14:36 -11/03/2015 -15:10
	 * */

    public static String add(String param){
    	boolean isAddedToStorage = false;
    	boolean isAddedToArray = false;
    	
    	temp = param.split("-");
    	temp=trimLeadingTrailingSpacesInArray(temp);
    	Task newTask = null;
    	
   
    	newTask = LogicAddTask.constructNewTask(temp);
    	
    	isAddedToArray = addTaskToTaskArrayList(newTask);
    	isAddedToStorage = Storage.XmlAddTask(Constants.XML_FILE_PATH, newTask);
    	
    	if(isAddedToArray && isAddedToStorage){
    		return Constants.LOGIC_SUCCESS_ADD_TASK;
    		
    	}else{
    		return Constants.LOGIC_FAIL_ADD_TASK;
    	}
    	
    }//end add
    
    /*
     * edit id -title  2
	edit id -title enddate endtime  3
	edit id -title startdate starttime enddate endtime  5
     */
    public static String edit(String param){
    	boolean isAddedToStorage = false;
    	boolean isAddedToArray = false;
    	temp = param.split("-");
    	temp=trimLeadingTrailingSpacesInArray(temp);
    	int taskId=Integer.parseInt(temp[0]);
    	int taskIndex=getTaskIndexInList(taskId);
    	Task extractedTask = extractTaskFromList(temp);
    	String[] newArray=new String[8];
    	System.arraycopy(temp, 1, newArray, 0, temp.length-1);
    	System.out.println("array size " +newArray.length);
    	if(temp.length==2){
    		extractedTask=editFloatingTask(extractedTask, newArray);
    	}else if(temp.length==4){
    		System.out.println("is deadline");
    		extractedTask=editDeadlineTask(extractedTask, newArray);
    		
    	}else if(temp.length==6){
    		extractedTask=editTimedTask(extractedTask, newArray);
    	}
    	
    	taskList.remove(taskIndex);
    	taskList.add(extractedTask);
    	System.out.println("testing");
    	for (int i = 0; i < newArray.length; i++) {
			System.out.println(newArray[i]);
		}
    	save();
    	return Constants.LOGIC_SUCCESS_EDIT_TASK;
    }

	private static Task editTimedTask(Task extractedTask, String[] newArray) {
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_TITLE])){
			extractedTask.setTitle(newArray[Constants.ARRAY_INDEX_TITLE]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_START_DATE])){	//check enddate
			extractedTask.setStartDate(newArray[Constants.ARRAY_INDEX_START_DATE]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_START_TIME])){//check endtime
			extractedTask.setStartTime(newArray[Constants.ARRAY_INDEX_START_TIME]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_END_DATE])){	//check enddate
			extractedTask.setEndDate(newArray[Constants.ARRAY_INDEX_END_DATE]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_END_TIME])){//check endtime
			extractedTask.setEndTime(newArray[Constants.ARRAY_INDEX_END_TIME]);
		}
		return extractedTask;
	}

	
	private static Task editDeadlineTask(Task extractedTask, String[] newArray) {

		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_TITLE])){
			extractedTask.setTitle(newArray[Constants.ARRAY_INDEX_TITLE]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[1])){	//check enddate
			extractedTask.setEndDate(newArray[1]);
		}
		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[2])){//check endtime
			extractedTask.setEndTime(newArray[2]);
		}
		return extractedTask;
	}

	private static Task editFloatingTask(Task extractedTask, String[] newArray) {

		if(!Constants.DEFAULT_VALUE.equalsIgnoreCase(newArray[Constants.ARRAY_INDEX_TITLE])){
			extractedTask.setTitle(newArray[Constants.ARRAY_INDEX_TITLE]);
		}
		return extractedTask;
	}

	private static Task extractTaskFromList(String[] temp) {
		Task tempTask;
		int taskId=Integer.parseInt(temp[0]);
		int taskIndex=getTaskIndexInList(taskId);
		tempTask = taskList.get(taskIndex);
		return tempTask;
	}

	/* public static String edit(String param){


    	return Constants.LOGIC_SUCCESS_EDIT_TASK;
    }*/

	public static String mark(String param){ 
		temp = param.split("-");
		temp=trimLeadingTrailingSpacesInArray(temp);
		Task newTask = extractTaskFromList(temp);
		String status=temp[1];

		if(Constants.STATUS_DONE.equalsIgnoreCase(status)){
			newTask.setIsDone(true);
		}
		else if(Constants.STATUS_UNDONE.equalsIgnoreCase(status)){
			newTask.setIsDone(false);
		}

		return Constants.LOGIC_SUCCESS_MARK_TASK;
	}//end mark

	public static boolean addTaskToTaskArrayList(Task task){
		taskList.add(task);
		return true;
	}

	public static boolean editTaskInList(Task task, int taskIndex){


		return true;
	}

    //delete tasks by id or all
    public static String delete(String param){
    	if(param.equalsIgnoreCase("-a")){
    		clearList();
    		return Constants.LOGIC_SUCCESS_DELETE_ALL_TASK;
    	}
    	else{
    		int id = Integer.parseInt(param.substring(1));
    		int index = findTaskIndex(id);
    		
    		if( index > 0){
    			taskList.remove(index);
    		}else{
    			return Constants.LOGIC_DELETE_TASK_NOT_FOUND;
    		}	
    	}
    	if (save()){
    		return Constants.LOGIC_SUCCESS_DELETE_TASK;
    	}
    	else{
    		return Constants.LOGIC_FAIL_DELETE_TASK;
    	}
    }
    
    //find the index of the task by a certain parameter, can expands it further
    public static Integer findTaskIndex(int id){
		for (int i = 0;i < taskList.size();i++){
			if(taskList.get(i).getID() == id){
				return i;
			}
		}
		return -1;
    }
    
    public static void clearList(){
    	taskList.clear();
    }
    
    public static boolean save(){
    	return Storage.tableToXml(Constants.XML_FILE_PATH, taskList);
    }

	public static String list(String parameters) {
		return ListTask.listTask(taskList).toString();
		//    	return "[LIST]Parameters entered are: " + parameters;
	}


	public static String validateString(String command, String input){

		boolean isValid = validateParameter(command, input);
		if(isValid){
			return Constants.LOGIC_VALID_PARAMETER_MESSAGE;
		}else{
			return Constants.LOGIC_INVALID_PARAMETER_MESSAGE;
		}

	}//end validateString

	/*
	 * Valid incoming parameter
	 * */
	private static boolean validateParameter(String command, String param){
		String[] inputArray = param.split("-");

		if(inputArray.length > 0){
			if(command.equalsIgnoreCase("add")){
				return validateAddParameters(param);
			}
			else if(command.equalsIgnoreCase("edit")){
				return validateEditParameters(param);
			}
			else if(command.equalsIgnoreCase("mark")){
				return validateMarkParameters(param);
			}
			else if(command.equalsIgnoreCase("delete")){
				return validateDeleteParameters(param);
			}
		}

		return false;
	}

	private static boolean validateMarkParameters(String param){
		String[] inputArray = param.split("-");
		inputArray=trimLeadingTrailingSpacesInArray(inputArray);
		int taskId=Integer.parseInt(inputArray[0]);
		boolean isTaskExists= isTaskInList(taskId);
		if(isTaskExists){
			if(inputArray[1].equalsIgnoreCase("done") || inputArray[1].equalsIgnoreCase("undone")){
				return true;
			}
		}

		return false;
	}

	private static boolean validateEditParameters(String param){
		System.out.println("inside editpara");
		String[] inputArray = param.split("-");
		inputArray=trimLeadingTrailingSpacesInArray(inputArray);
		String[] newArray=new String[8];
		System.arraycopy(inputArray, 1, newArray, 0, inputArray.length-1);
		for (int i = 0; i < newArray.length; i++) {
			System.out.println(newArray[i] +" "+i);
		}
		int taskId=Integer.parseInt(inputArray[0]);
		boolean isTaskExists= isTaskInList(taskId);
		if(isTaskExists){
			System.out.println("task exists"+newArray.length);
			int taskIndex=getTaskIndexInList(taskId);
			Task task=taskList.get(taskIndex);
			if(inputArray.length==2){
				if(TokenValidation.isTitleValid(newArray[Constants.ARRAY_INDEX_TITLE])){	//edit id -title 
					return true;
				}
			}
			else if(inputArray.length==4){
				if(TokenValidation.isTitleValid(newArray[Constants.ARRAY_INDEX_TITLE]) && (TokenValidation.isDateValid(newArray[1])) && (TokenValidation.isTimeValid(newArray[2]))){
					return true;
				}	
				/*if(TokenValidation.isTitleValid(newArray[Constants.ARRAY_INDEX_TITLE]))
					System.out.println("title true");
				if(TokenValidation.isDateValid(newArray[1]))
					System.out.println("date true");
				if(TokenValidation.isTimeValid(newArray[2]))
					System.out.println("time true");*/
			}
			else if(inputArray.length==6){
				if(TokenValidation.isTitleValid(newArray[Constants.ARRAY_INDEX_TITLE]) && (TokenValidation.isDateValid(newArray[Constants.ARRAY_INDEX_END_DATE])) && (TokenValidation.isTimeValid(newArray[Constants.ARRAY_INDEX_END_TIME])) && (TokenValidation.isDateValid(newArray[Constants.ARRAY_INDEX_START_DATE])) && (TokenValidation.isTimeValid(newArray[Constants.ARRAY_INDEX_START_TIME])) && (TokenValidation.isStartDateBeforeThanEndDate(inputArray[Constants.ARRAY_INDEX_START_DATE], inputArray[Constants.ARRAY_INDEX_END_DATE]))){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean validateDeleteParameters(String param){

		if(param.isEmpty()){
			return false;
		}

		String input = param.substring(1);
		try { 
			int id = Integer.parseInt(input); 
			// if it is a number
			if( id < 0 || id > taskList.get(-1).getID()){
				return false;
			}
			return true;

		} catch(NumberFormatException e) { //if it is not a number
			if(!param.equalsIgnoreCase("a")){
				return false;
			}
			return true;
		}

	}

	private static boolean isTaskInList(int taskId){
		for(Task t: taskList){
			if(t.getID()==taskId){
				return true;
			}
		}
		return false;
	}

	private static int getTaskIndexInList(int taskId){
		for (int i = 0; i < taskList.size(); i++) {
			if(taskList.get(i).getID()==taskId)
				return i;
		}
		return -1;
	}

	private static boolean validateAddParameters(String param){
		String[] inputArray = param.split("-");
		inputArray=trimLeadingTrailingSpacesInArray(inputArray);


		if(inputArray.length==1){
			return TokenValidation.isTitleValid((inputArray[Constants.ARRAY_INDEX_TITLE]));
		}

		else if(inputArray.length==3){
			if(TokenValidation.isTitleValid(inputArray[Constants.ARRAY_INDEX_TITLE]) && (TokenValidation.isDateValid(inputArray[1])) && (TokenValidation.isTimeValid(inputArray[2]))){
				return true;
			}
		}
		else if(inputArray.length==5){
			if(TokenValidation.isTitleValid(inputArray[Constants.ARRAY_INDEX_TITLE]) && (TokenValidation.isDateValid(inputArray[Constants.ARRAY_INDEX_END_DATE])) && (TokenValidation.isTimeValid(inputArray[Constants.ARRAY_INDEX_END_TIME])) && (TokenValidation.isDateValid(inputArray[Constants.ARRAY_INDEX_START_DATE])) && (TokenValidation.isTimeValid(inputArray[Constants.ARRAY_INDEX_START_TIME])) && TokenValidation.isStartDateBeforeThanEndDate(inputArray[Constants.ARRAY_INDEX_START_DATE], inputArray[Constants.ARRAY_INDEX_END_DATE])){
				return true;
			}
		}

		return false;
	}

	private static String[] trimLeadingTrailingSpacesInArray(String[] temp){
		for (int i = 0; i < temp.length; i++) {
			temp[i]=temp[i].trim();
		}
		return temp;
	}

}
