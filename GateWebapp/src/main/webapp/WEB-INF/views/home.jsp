<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.8.0.js">
	
</script>
<style type="text/css">
#nlp {
	float: left;
}

#update {
	float: left;
	display: inline;
}

#input {
	width: 300px;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		$("#analyze").click(function() {
			$.ajax({
				type : "POST",
				url : "processtext?text=" + $('#text').val(),
				data : $('#text').val(),
				success : function(response) {
					console.log(response);
					$('#resultText').val(response);
				}
			});
		});
	});
</script>
<title>Example GATE application</title>
</head>
<body>
	<div id="nlp">
		<fieldset>
			<legend>Text to be analyzed</legend>
			<textarea name="text" id="text" cols="60" rows="20">
			</textarea>
			<br> <input id="analyze" type="button" value="Analyze" />
		</fieldset>

	</div>
	<div id=result>
		<fieldset>
			<legend>Analyzed result</legend>
			<textarea name="text" id="resultText" cols="60" rows="20">			
		</textarea>
		</fieldset>

	</div>

</body>