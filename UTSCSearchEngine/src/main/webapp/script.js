var TOMCAT_URL = "http://localhost:8080";
var results = [];

$(document).ready(function() {
	// handle search button
	$("#searchButton").click(function() {
		alert('test');
	});
});

// search functionality
function search() {
	var http = new XMLHttpRequest();
	var searchQuery = document.getElementById("searchBox").value;
	var option = $("#optionsList input:radio:checked").val();
	results = [];
	$("#resultsBody").empty();

	if (searchQuery && option == 1) {
		getSearchResults(http, searchQuery, "fileName");
	} else if (searchQuery && option == 2) {
		getSearchResults(http, searchQuery, "fileType");
	} else if (searchQuery && option == 3) {
		getSearchResults(http, searchQuery, "userName");
	} else if (searchQuery && option == 4) {
		getSearchResults(http, searchQuery, "userType");
	}
};

// get search results
function getSearchResults(http, searchQuery, queryParam) {
	// handle REST response
	http.open("GET", TOMCAT_URL + "/search?" + queryParam + "=" + searchQuery, false);
	http.send(null);
	var resp = http.response.split("\n");
	resp.forEach(function (element) {
		if (element.length > 0) {
			results.push({
				"fileName": element.split("-")[0],
				"fileType": element.split("-")[1],
				"userType": element.split("-")[2],
				"userName": element.split("-")[3]
			});
		}
	});

	// modify DOM
	document.getElementById("resultsList").innerHTML = "Found <b>" + results.length + "</b> results";
	$.each(results, function (index, item) {
		var rows = "<tr>"
				+ "<td>" + item.fileName + "</td>"
				+ "<td>" + item.fileType + "</td>"
				+ "<td>" + item.userName + "</td>"
				+ "<td>" + item.userType + "</td>"
				+ "</tr>";
		$("#resultsBody").append(rows);
	});
};

// clear search results
function clearContents() {
	document.getElementById("resultsList").innerHTML = "No results";
};

// login request
function loginUser() {
	var http = new XMLHttpRequest();
	var username = document.getElementById("loginUsername").value;
	var password = document.getElementById("loginPassword").value;
	var request = {
		"userName": username,
		"password": password
	};

	http.open("POST", TOMCAT_URL + "/login?=true", false);
	http.setRequestHeader("Content-Type", "application/json");
	http.send(JSON.stringify(request));
	var resp = http.response;

	if (resp.status == "SUCCESS") {
		$("#loginModal").modal("hide");
		$("#successModal").modal("show");
		$("#successText").empty();
		$("#successText").append(resp.message);
	} else if (resp.status == "FAILURE") {
		$("#loginModal").modal("hide");
		$("#failureModal").modal("show");
		$("#failureText").empty();
		$("failureText").append(resp.message);
	}
};