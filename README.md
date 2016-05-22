# ttdc
TTDC.us.  My personal website.

Where my friends and i hang out online. 

Tech: gwt, hibernate, json

//Tips for the new REST interface. Still beta, but it's in production.

http://ttdc.us/restful/login
/** Login.  Used to get a token.
Request:
{
	"username": "Trav",
	"password": "pa$$word"
}
Response:

Person object and a token

/** Get a collection of the latest posts
http://ttdc.us/restful/latestposts

Request:
{
	"action": "LATEST_GROUPED",
	"pageNumber": 1
}

Response:

A jason object with with some details about the page size, page number requested and a 'list' argument which contains a 1 level deep hierarchy 
of the latest posts visbile to all users of TTDC sorted by the conversation with the most recient reply

Request:
{
	"action": "LATEST_FLAT",
	"pageNumber": 1
}

Response: 

Same as above except the list contains no hierarchy.  Just the posts sorted in reverse creation order.

Request:
{
	"action": "LATEST_FLAT",
	"pageNumber": 1,
	"token": "token=rO0ABXNy......."
}

Response: 

Same as above except now all post visibile to a user with the given security token are in the result list. (Private and Muted filters are applied)


/** Get a thread (topic)
http://ttdc.us/restful/topic

Request{
	"postId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"pageNumber": 1,
	"type": "NESTED_THREAD_SUMMARY",
	"token": "rO0ABXN...(Optional)"
}

Response:
Returns a results json object with a format similar to request the latest comments grouped.

/**  Post CRUD operations
http://ttdc.us/restful/post

/* Create a post with on the fly login
Request:
{
	"parentId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"action": "CREATE",
	"body": "Bla bla bla bla bla, yada yada",
	"login": "Trav",
	"password": "pa$$word"
}

Response: 
Response contains the newly created post object

/* Create a post with a token
Request:
{
	"parentId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"action": "CREATE",
	"body": "Bla bla bla bla bla",
	"token": "rO0ABXN...."
}

Response: 
Response contains the newly created post object

