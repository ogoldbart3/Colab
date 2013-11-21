<?php
        include 'db_helper.php';
        
        function listComments($postId)
        {                
                error_log("inside listComment()", 0);
                $dbQuery = sprintf("SELECT * FROM comment INNER JOIN student ON comment.studentId=student.studentId WHERE postId=" . $postId . " ORDER BY time ASC");
                error_log("query: " . $dbQuery, 0);
                $result = getDBResultsArray($dbQuery);
                error_log("executed query", 0);
                header("Content-type: application/json");
                echo json_encode($result);
        }
        
        function getComment($postId, $commentId)
        {
                error_log("inside getComment()", 0);
                $dbQuery = sprintf("SELECT * FROM comment INNER JOIN student ON comment.studentId=student.studentId WHERE postId=" . $postId . " AND commentId=" . $commentId);

                $result = getDBResultsArray($dbQuery);
                
                header("Content-type: application/json");
                echo json_encode($result);
        }
       
	   function getAllChatMessages($userId)
	   {
			global $_USER;
			$dbQuery = sprintf("SELECT studentId FROM student WHERE studentGt='" . $_USER['uid'] . "' LIMIT 1");
            $result = getDBResultsArray($dbQuery);
            $userId = $result[0]['studentId'];
			
			$dbQuery = sprintf("SELECT * FROM messages WHERE touid='".$userId."'");
			if(isset($_GET['type'])) 
			{
				if(strcmp($_GET['type'], "unread") == 0)
					$dbQuery = sprintf("SELECT * FROM messages WHERE touid='".$userId."' AND isRead=0");
				else
					$dbQuery = sprintf("SELECT * FROM messages WHERE touid='".$userId."' AND isRead=1");
			}
			$result = getDBResultsArray($dbQuery);
			
			header("Content-type: application/json");
			echo json_encode($result); 
	   }
	   
	    function getNewChatMessages($userId, $receiverId)
		{
			global $_USER;
			$dbQuery = sprintf("SELECT studentId FROM student WHERE studentGt='" . $_USER['uid'] . "' LIMIT 1");
            $result = getDBResultsArray($dbQuery);
            $userId = $result[0]['studentId'];
			
			$dbQuery = sprintf("SELECT * FROM messages WHERE fromuid='".$userId."' AND touid='".$receiverId."'");
			$result = getDBResultsArray($dbQuery);
			
			
			header("Content-type: application/json");
			echo json_encode($result2); 
		}
		
		function addChatMessage($userId, $receiverId)
		{
			global $_USER;
			$dbQuery = sprintf("SELECT studentId FROM student WHERE studentGt='" . $_USER['uid'] . "' LIMIT 1");
            $result = getDBResultsArray($dbQuery);
            $userId = $result[0]['studentId'];
							
			$dbQuery = sprintf("INSERT INTO messages(fromuid, touid, sentdt, messagetext) VALUES ('".$userId."', '".$receiverId."', '".DATE("Y-m-d H:i")."', '%s');", mysql_real_escape_string($_POST['message']));
			
			if(isset($_POST['messageRead']))
			{
				$dbQuery = sprintf("UPDATE messages SET isRead=1 WHERE id='".$_POST['messageRead']."'");
			}
		
			$result = getDBResultInserted($dbQuery,'id');
			
			header("Content-type: application/json");
			echo json_encode($result);
		}
		
        function addComment($postId, $courseId, $studentId)
        {                
                /*
                $dbQuery = sprintf("SELECT courseId FROM post WHERE postId=" . $postId . " LIMIT 1");
                $result = getDBResultsArray($dbQuery);
                $courseId = $result[0]['courseId'];
                
                $dbQuery = sprintf("SELECT studentId FROM post WHERE postId=" . $postId . " LIMIT 1");
                $result = getDBResultsArray($dbQuery);
                $studentId = $result[0]['studentId'];
                */
                
                error_log("postId: " . $postId, 0);
                error_log("courseId: " . $courseId, 0);
                error_log("studentId: " . $studentId, 0);
                error_log("comment text: ". $_POST['addCommentText'], 0);
                error_log("comment datetime: ". $_POST['addCommentDateTime'], 0);
                
        $dbQuery = sprintf("INSERT INTO comment (postId, courseId, studentId, commentText, time) 
                        VALUES (" . $postId . "," . $courseId . "," . $studentId . ",'" . $_POST['addCommentText'] .  "','" . $_POST['addCommentDateTime'] . "')"); 
                
                //error_log("query: " . $dbQuery, 0);
                
        $result = getDBResultInserted($dbQuery, 'commentId');
        
                //error_log("executed dbquery", 0);
                
        header("Content-type: application/json");
        echo json_encode($result);
        }
        
        function updateComment($commentId) 
        {                
        $dbQuery = sprintf("UPDATE comment SET commentText='" . $_POST['editCommentText'] 
                        . "' WHERE commentId=" . $commentId);
        
        $result = getDBResultAffected($dbQuery);
        
        header("Content-type: application/json");
        echo json_encode($result);
        }
        
        function deleteComment($postId)
        {
        
        }
        
        function listPosts($courseId)
        {
                $dbQuery = sprintf("SELECT * FROM post INNER JOIN student ON post.studentId=student.studentId WHERE courseId=" . $courseId . " ORDER BY time ASC");

                $result = getDBResultsArray($dbQuery);
                
                header("Content-type: application/json");
                echo json_encode($result);
        }
        
        function getPost($courseId, $postId)
        {
                $dbQuery = sprintf("SELECT * FROM post INNER JOIN student ON post.studentId=student.studentId WHERE courseId=" . $courseId . " AND postId=" . $postId);

                $result = getDBResultsArray($dbQuery);
                
                header("Content-type: application/json");
                echo json_encode($result);
        }
        
        function addPost($studentId, $courseId)
        {
                error_log("inside addPost()", 0);
        $dbQuery = sprintf("INSERT INTO post (studentId, courseId, postText, time) 
                        VALUES (" . $studentId . "," . $courseId . ",'" . $_POST['addPostText'] .  "','" . $_POST['addPostDateTime'] . "')"); 
                
                error_log("query: " . $dbQuery, 0);
        $result = getDBResultInserted($dbQuery, 'postId');
                error_log("executed dbquery", 0);
                
        header("Content-type: application/json");
        echo json_encode($result);
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
                $result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://pinch1.lms.gatech.edu/direct/site.json"));
                //$result = $_PLATFORM->secureWeb("https://shepherd.cip.gatech.edu/proxy/?url=".urlencode("https://t-square.gatech.edu/direct/site.json"));
                error_log("dump: " . $result , 0);
                
                header("Content-type: application/json");
                echo json_encode($result);
        }
        
		/* CAHNGED HERE */
        function getLoginUsername()
        {
                global $_USER;
                $dbQuery = sprintf("SELECT * FROM student WHERE studentGt='" . $_USER['uid'] . "' LIMIT 1");
                $result = getDBResultsArray($dbQuery);
               
 			    header("Content-type: application/json");
                echo json_encode($result[0]);
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
                $dbQuery = sprintf("SELECT s.studentId, s.studentFirst, s.studentLast, s.studentGt, s.studentPhone, s.status, s.aboutMe, sl.longitude, sl.latitude FROM student as s LEFT OUTER JOIN student_locations AS sl on s.studentId = sl.userID WHERE s.studentId IN
                        (SELECT studentId FROM studentCourseMapping WHERE courseId=" . $courseId . ") ORDER BY s.studentFirst ASC");

                $result = getDBResultsArray($dbQuery);
                
                header("Content-type: application/json");
                echo json_encode($result);
        }
		
?>