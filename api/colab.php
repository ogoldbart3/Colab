<?php
	include 'db_helper.php';
	
	function listComments($postId)
	{
		$dbQuery = sprintf("SELECT * FROM comment WHERE postId=" . $postId . " ORDER BY time ASC");

		$result = getDBResultsArray($dbQuery);

		header("Content-type: application/json");
		echo json_encode($result);
	}
	
	function getComment($postId, $commentId)
	{
		$dbQuery = sprintf("SELECT * FROM comment WHERE postId=" . $postId . " AND commentId=" . $commentId);

		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}
	
	function addComment($postId)
	{

	}
	
	function deleteComment($postId)
	{
	
	}
	
	function listPosts($courseId)
	{
		$dbQuery = sprintf("SELECT * FROM post WHERE courseId=" . $courseId . " ORDER BY time ASC");

		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}
	
	function getPost($courseId, $postId)
	{
		$dbQuery = sprintf("SELECT * FROM post WHERE courseId=" . $courseId . " AND postId=" . $postId);

		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}
	
	function addPost($courseId)
	{

	}
	
	function deletePost($courseId)
	{
	
	}
	
	function getCoursesFromTS()
	{
		/*
		global $_PLATFORM;
		$result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://pinch1.lms.gatech.edu/sakai-login-tool/container"));
		$result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://pinch1.lms.gatech.edu/direct/user/current.json"));
		var_dump($result);
		*/
		
		global $_PLATFORM;
		//$result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://pinch1.lms.gatech.edu/sakai-login-tool/container"));
		//$result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://t-square.gatech.edu/direct/site.json"));

		header("Content-type: application/json");
		echo $result;
	}
	
	function getLoginUsername()
	{
		global $_USER;
		$dbQuery = sprintf("SELECT studentId FROM student WHERE studentGt='" . $_USER['uid'] . "' LIMIT 1");
		$result = getDBResultsArray($dbQuery);
		$selfId = $result[0]['studentId'];
		header("Content-type: application/txt");
		echo $selfId;
	}
	
	/*
		Show a list of all courses for a student by student ID
	*/
	function listCourses($studentId)
	{
		$dbQuery = sprintf("SELECT * FROM course WHERE courseId IN
			(SELECT courseId FROM studentCourseMapping WHERE studentId=" . $studentId . ") ORDER BY courseName ASC");
			
		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}
	
	/*
		Get a single course for a student by student ID and course ID 
	*/
	function getCourse($studentId, $courseId) 
	{
        $dbQuery = sprintf("SELECT courseName FROM course WHERE courseId IN
			(SELECT courseId FROM studentCourseMapping WHERE studentId=" . $studentId . " AND courseId=" . $courseId . ")");
				
		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}

	/*
		Add a new course for a student
	*/
	function addCourse($studentId) 
	{
		$dbQuery = sprintf("SELECT courseId FROM course WHERE courseName='%s' LIMIT 1",
			mysql_real_escape_string($_POST['aCourseName']));
			
		$result = getDBResultsArray($dbQuery);
		$courseId = $result[0]['courseId'];

        $dbQuery = sprintf("INSERT INTO studentCourseMapping (courseId, studentId, visibility) 
			VALUES (" . $courseId . "," . $studentId . "," . $_POST['aVisibility'] . ")");
			
        $result = getDBResultInserted($dbQuery, 'courseId');
        
        header("Content-type: application/json");
        echo json_encode($result);
	}

	/*
		Update an existing course for a student
	*/
	function updateCourse($studentId) 
	{
		$dbQuery = sprintf("SELECT courseId FROM course WHERE courseName='%s' LIMIT 1",
			mysql_real_escape_string($_POST['eCourseName']));
			
		$result = getDBResultsArray($dbQuery);	
		$courseId = $result[0]['courseId'];
		
        $dbQuery = sprintf("UPDATE studentCourseMapping SET visibility=" . $_POST['eVisibility'] 
			. " WHERE courseId=" . $courseId . " AND studentId=" . $studentId);
        
        $result = getDBResultAffected($dbQuery);
        
        header("Content-type: application/json");
        echo json_encode($result);
	}

	/*
		Delete an existing course for a student
	*/
	function deleteCourse($studentId) 
	{
		$dbQuery = sprintf("SELECT courseId FROM course WHERE courseName='%s' LIMIT 1",
			mysql_real_escape_string($_POST['dCourseName']));
			
		$result = getDBResultsArray($dbQuery);
		$courseId = $result[0]['courseId'];
        $dbQuery = sprintf("DELETE FROM studentCourseMapping WHERE courseId=" . $courseId . " AND studentId=" . $studentId);  
	
        $result = getDBResultAffected($dbQuery);
        
        header("Content-type: application/json");
        echo json_encode($result);
	}
	
	function listFriends($courseId)
	{
		$dbQuery = sprintf("SELECT studentFirst, studentLast, studentId FROM student WHERE studentId IN
			(SELECT studentId FROM studentCourseMapping WHERE courseId=" . $courseId . ") ORDER BY studentFirst ASC");

			/*
			
		$dbQuery = sprintf("SELECT studentId, studentFirst, studentLast, studentGt, studentPhone, location, status FROM student WHERE studentId IN
			(SELECT studentId FROM studentCourseMapping WHERE courseId=" . $courseId . ") ORDER BY studentFirst ASC");
			*/
		$result = getDBResultsArray($dbQuery);
		
		header("Content-type: application/json");
		echo json_encode($result);
	}
?>