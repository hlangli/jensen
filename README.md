jensen
======

<small>Jensen JSON-RPC Java Bridge</small>

Jensen is a reflection-based JSON-RPC broker and caller for java.  It is highly configurable with support for any compatible transport protocol.

## Broker
The broker is the API for invoking methods from a JSON-RPC request.

### Configuration
A JsonRpcBroker instance is obtained from a J


## Caller
The caller is the API for creating and sending JSON-RPC requests.

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
