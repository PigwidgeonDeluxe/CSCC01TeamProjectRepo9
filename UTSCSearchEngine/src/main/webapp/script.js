var TOMCAT_URL = "http://localhost:8080";
var results = [];

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

$(document).ready(function() {
	// change file type dropdown
	$("#fileTypeMenu a").click(function() {
		$("#selectedFileType").text($(this).text());
	});

	// change user type dropdown
	$("#userTypeMenu a").click(function() {
		$("#selectedUserType").text($(this).text());
	});
});