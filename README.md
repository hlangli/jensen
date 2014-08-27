jensen
======
<small>Jensen JSON-RPC Java Bridge</small>

Jensen is a generic, reflection-based JSON-RPC bridge for java.  No configuration is needed.  Any method in any class with a no-args constructor can be called via JSON-RPC by specifying the fully qualified classname along with the method name in the "method" property of the json-rpc request, like so:

<pre>{
	"jsonrpc": "2.0",
	"method": "dk.nineconsult.jensen.test.JensenTest.notification",
	"params": [
		{
			"integer": 99,
			"string": "weld",
			"object2": {
				"svend": "grethe"
			}
		},
		42
	]
}</pre>

There might be some security-concerns, like if someone were to call "java.lang.System.exit" or something more exotic.  I might fix that in version 2.