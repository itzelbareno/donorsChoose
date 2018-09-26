# Donors Choose API Consumer Java App
Execute jar file as:
path/to/jar/file>java -jar DonorsChoose.jar [-args]


-h, --help	Displays all arguments' descriptions and syntax.
-s, --search	Search by parameter, by specifying parameter name and value.
		Syntax: -s paramName1 paramValue1 paramName2 paramValue2...
		Example: -s subject1 1 state MN
-q, --query	Add a query string at the end of the URL by specifying such query string. 
		Enclose in " " for multiple search parameters.
		Syntax: -q paramName1=paramValue1&paramName2=paramValue2...
		Example: -q "subject1=1&state=MN"

