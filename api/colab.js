$(function() 
{
	// Handler for .ready() called.
	console.log('ready');
	
	var username, course, friend;

	/*
		Bind to the create so the page gets updated with the listing
	*/
	$('#homePageID').bind('pagebeforeshow',function(event, ui)
	{
		$.ajax({
			url: "api/user",
			dataType: "text",
			async: false,
			success: function(data, textStatus, jqXHR) 
			{
				console.log("***getting currently login username");
				console.log(data);
				username = data;
			},
			error: ajaxError
		});
		/*
		$.ajax({
			url: "http://m.cip.gatech.edu/proxy/t-square.gatech.edu/direct/site.json",
			context: document.body,
			success: function(data, textStatus, jqXHR) 
			{
				console.log("***getting courses from t-square");
				console.log(data);
			},
			error: ajaxError
		});
		*/
		//console.log("***after ajax call, js has username: " + username);
		/*
		$.ajax({
			url: "https://t-square.gatech.edu/direct/site.json",
			dataType: "json",
			async: false,
			success: function(data4, textStatus, jqXHR) 
			{
				console.log("***getting entity collection for the login user");
				console.log(data4);
			},
			error: ajaxError
		});
		*/
		/*
		var xhr = new XMLHttpRequest();
		xhr.open("GET", "https://t-square.gatech.edu/direct/site.json", true);
		xhr.onreadystatechange = function() 
		{
			if (xhr.readyState == 4) 
			{
				console.log("***t-square response: \n" + xhr.responseText);
			}
		}
		xhr.send();
		*/
		//Remove the old rows
		$('.people').remove();
		
		//JQuery Fetch The New Ones
		$.ajax({
				url: "api/user/" + username + "/course",
				dataType: "json",
				async: false,
				success: function(data, textStatus, jqXHR) 
				{
					console.log("***got all courses for this student");
					console.log(data);
					
					var count = 0;
					jQuery.each(data, function() 
					{	
						//$('#peopleListID').append("<li class='people' id='course" + count + "ID' data-role='list-divider' data-divider-theme='g'>" + this.courseName + "<a href='#postPageID' data-role='button'>Comment</a></li>");
						$('#peopleListID').append("<li class='people' id='" + this.courseId + "' data-role='list-divider' data-divider-theme='g'><a href='#postPageID' id='course" + count + "ID' data-role='button'>" + this.courseName + "</a></li>");
						++count;
						console.log("--" + this.courseName + " -> " + this.courseId);

						$.ajax({
							url: "api/user/" + username + "/course/" + this.courseId + "/friend",
							dataType: "json",
							async: false,
							success: function(data2, textStatus, jqXHR) 
							{
								console.log("***got all friends for this course");
								console.log(data2);
								jQuery.each(data2, function() 
								{	
									/*
									$('#peopleListID').append("<li class='people'>" 
										+ "<a href='#'>" + this.studentFirst + " " + this.studentLast + "</a>"
										+ "<div data-role='controlgroup' data-type='horizontal'>"
										+ "<a href='#' data-mini='true' id='visID' data-inline='true' data-icon='plus' data-role='button'>Visibility</a>"
										+ "<a href='#' data-mini='true' id='chatID' data-inline='true' data-icon='plus' data-role=\"button\">Chat</a>"
										+ "<a href='#' data-role=\"button\" data-mini='true' id='mapID' data-inline='true' data-icon='plus'>Map</a>"
										+ "</div></li>");
									*/
									
									if(this.studentId != username)
									{
										console.log("***friend id: " + this.studentId + "  self id: " + username);
										$('#peopleListID').append("<li class='people' id='friend" + count + "ID'>" 
											+ "<a href='#'>" + this.studentFirst + " " + this.studentLast + "</a>"
											+ "<a href='#' data-mini='true' id='visID' data-inline='true' data-icon='plus' data-role='button'>Visibility</a>"
											+ "<a href='#' data-mini='true' id='chatID' data-inline='true' data-icon='plus' data-role='button'>Chat</a>"
											+ "<a href='#mapPageID' data-mini='true' id='mapID' data-inline='true' data-icon='plus' data-role='button'>Map</a>"
											+ "</li>");
										
										++count;
									}
									
									/*
									$('#peopleListID').append("<li class='people'>" 
										+ "<a href='#mapPageID'>" + this.studentFirst + " " + this.studentLast + "</a></li>");
									*/
									
									console.log("---" + this.studentFirst + " " + this.studentLast);
								});
							},
							error: ajaxError
						});
					});
				},
				error: ajaxError
		});
		
		$('#peopleListID').listview('refresh');
		
		// add listener to course's comment click
		/*
		$('#peopleListID').each(function()
		{
			$(this).bind('click', function(e)
			{
				console.log("item clicked: " + e);
			});
		});
		*/
		
		$('#peopleListID').delegate('li', 'click', function() 
		{
			var index = $(this).index();
			console.log("***list item clicked: " + index);
		
			if(doesElementExist("course" + index + "ID"))
			{
				course = $("#course" + index + "ID").parent().attr("id");
				console.log("***clicked on course item (" + course + ") -> going to post");
			}
			else
			{
				friend = $("#friend" + index + "ID").val();
				console.log("***clicked on friend item (" + friend + ") -> going to profile");
			}	
		});
	});
	
	$('#mapPageID').bind('pagebeforeshow',function(event, ui)
	{
		//var yourStartLatLng = new google.maps.LatLng(59.3426606750, 18.0736160278);
		//$('#mapPageContentID').gmap({'center': yourStartLatLng});
	});
	
	$('#postPageID').bind('pagebeforeshow',function(event, ui)
	{
		$('.postcomment').remove();
		
		$.ajax({
			url: "api/user/" + username + "/course/" + course + "/post",
			dataType: "json",
			async: false,
			success: function(data, textStatus, jqXHR) 
			{
				console.log("***got all posts for this course");
				console.log(data);
				
				var count = 0;
				
				jQuery.each(data, function() 
				{	
					$('#postListID').append("<li class='postcomment' id='post" + count + "ID' data-role='list-divider' data-divider-theme='g'>" 
						+ this.postText + "</li>");
						
					console.log("--" + this.postText);
					
					$.ajax({
						url: "api/user/" + username + "/course/" + course + "/post/" + this.postId + "/comment",
						dataType: "json",
						async: false,
						success: function(data2, textStatus, jqXHR) 
						{
							console.log("***got all comments for this post");
							console.log(data2);
							
							//$("#postListID").append("<ui id='commentList" + count + "ID' class='postcomment' data-role='listview' data-inset='true' data-filter='true'></ul>");
													
							jQuery.each(data2, function() 
							{	
								/*
								$("#commentList" + count + "ID").append("<li class='postcomment'>" 
									+ this.commentText + "</li>");
								*/
								
								$("#postListID").append("<li class='postcomment'>" 
									+ this.commentText + "</li>");
									
								console.log("---" + this.commentText);
							});
						},
						error: ajaxError
					});
					
					++count;
				});
			},
			error: ajaxError
		});
		
		$('#postListID').listview('refresh');
	});
		
	/*
		Bind the add course button
	*/
	$('#addCourseBtnID').bind('click', function() 
	{
		console.log("***add course clicked");
		var visibility = 0;
		
		if($('#aVisibilityID').is(':checked'))
			visibility = 1;

		$.ajax({
				url: "api/user/1/course",
				dataType: "json",
				async: false,
				data: {'aCourseName': $('#aCourseNameID').val(),
						'aVisibility': visibility},
				type: 'POST',
				success: function() 
				{ 
					console.log("***added new class - " + $('#aCourseNameID').val()); 
				},
				error: ajaxError
		});
	});
		 
	/*
		Bind the edit course button
	*/
	$('#editCourseBtnID').bind('click', function() 
	{
		console.log("***edit course clicked");
		var visibility = 0;
		
		if($('#eVisibilityID').is(':checked'))
			visibility = 1;

		$.ajax({
			url: "api/user/1/course",
			dataType: "json",
			async: false,
			data: {'eCourseName': $('#eCourseNameID').val(),
					'eVisibility': visibility},
			headers: {'X-HTTP-Method-Override': 'PUT'},
			type: 'POST',
			success: function() 
			{ 
				console.log("***edited class - " + $('#eCourseNameID').val()); 
			},
			error: ajaxError
		});
	});
	
	/*
		Bind the delete course button
	*/
	$('#deleteCourseBtnID').bind('click', function() 
	{
		console.log("***delete course clicked");
		$.ajax({
			url: "api/user/1/course",
			dataType: "json",
			async: false,
			data: {'dCourseName': $('#dCourseNameID').val()},
			type: 'DELETE',
			success: function() 
			{ 
				console.log("***deleted class - " + $('#dCourseNameID').val()); 
			},
			error: ajaxError
		});
	});
	
	/*
	getCurrentLocationOnce();
	
	Candy.init('http-bind/', {
	core: { debug: false },
	view: { resources: 'lib/candy_chat/res' }
	});
	
	Candy.Core.connect();
	*/
});
 
function doesElementExist(id)
{
	return $("#" + id).length != 0;
}

function getCurrentLocationOnce() 
{
	console.log("***inside getCurrentLocation()");
	if(navigator.geolocation) 
	{
		navigator.geolocation.getCurrentPosition(showPosition, showError);
	} 
	else 
	{
		alert("Your browser does not support HTML5 Geolocation.");
	}
}

function showError(error)
{
	switch(error.code) 
	{
		case error.PERMISSION_DENIED:
			alert("User denied the request for Geolocation.");
			break;
		case error.POSITION_UNAVAILABLE:
			alert("Location information is unavailable.");
			break;
		case error.TIMEOUT:
			alert("The request to get user location timed out.");
			break;
		case error.UNKNOWN_ERROR:
			alert("An unknown error occurred.");
			break;
	}
}

function showPosition(position)
{
	console.log("***inside showPosition()");
	var coords = position.coords;
	console.log("***lat: " + coords.latitude + "  lon: " + coords.longitude);
	
}

/******************************************************************************/
 
function ajaxError(jqXHR, textStatus, errorThrown){
        console.log('ajaxError '+textStatus+' '+errorThrown);
        $('#error_message').remove();
        $("#error_message_template").tmpl( {errorName: textStatus, errorDescription: errorThrown} ).appendTo( "#error_dialog_content" );
        $.mobile.changePage($('#error_dialog'), {
                transition: "pop",
                reverse: false,
                changeHash: false
        });
}