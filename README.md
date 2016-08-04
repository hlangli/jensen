jensen
======
<small>Jensen JSON-RPC Java Bridge</small>

Jensen is a generic, reflection-based JSON-RPC bridge for java.  No configuration is needed.  Any method in any class with a no-args constructor can be called via JSON-RPC by specifying the fully qualified classname along with the method name in the "method" property of the json-rpc request, like so:

<pre>{
	"jsonrpc": "2.0",
	"method": "dk.langli.jensen.test.JensenTest.notification",
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

Jensen can be configured using the JensenBuilder.  It is possible to adjust the ObjectMapper accordingly and add Jacskon modules.  Security is handled in the allowedPackages variable where one can set package-prefixes for the classes and methods that are allowed to be invoked.
