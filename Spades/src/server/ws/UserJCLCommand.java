package server.ws;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * UserJCLCommand -- parse commands beginning with //
 * place into the structure with a type and name value pairs
 * for calls to getparam 
 * 
 */
public class UserJCLCommand {
	// Kinds of parsing errors in string
	public enum JCLType {
		JCLNotJCL, // String is not a JCL command
		JCLError, // JCL command but malformed
		JCLCommandNotRecognized, // Command is not recognized
		JCLCommandNotImplemented, // Command is not implemented
		JCLSetname, // set the name attached to user
		JCLJoin, // Join, Rejoin games
		JCLRejoin, JCLSuperUser, // SuperUser
		JCLPoke, JCLResume, 	// poke or resume the server
		JCLWhoAmI,
	}

	JCLType type = JCLType.JCLNotJCL;

	public class NameValuePair {
		String name="";
		String value="";
		NameValuePair(String sName, String sValue) {
			name = sName;
			value = sValue;
		}
		public String getValue() { return value;}
	}

	String getName(int index) {
		return argv.get(index).name;
	}
	String getValue(int index) {
		return argv.get(index).value;
	}
	public int argc() {
		return argv.size();
	}
	
	// Vector params[]=new Vector
	Vector<NameValuePair> argv=new Vector(3,1);
	
	/*
	 * Create a JCL command object by parsing string
	 * get the arguments to the command i.e. //setname foo
	 * todo:
	 * 	should support
	 *   //setname name=foo
	 *   // rejoin game=123,name=me
	 * arg 1 value is foo
	 */
	// getName(i)
	// getValue(i)
	// ... get the length...
	/*
	 * Move parse code from WsServer.java to here... public constructor
	 */

	/*
	 * if (isJCL(message)) { // process and write return string to sender, but do
	 * not save, echo, or broadcast var returnString = processJCL(message, us); //
	 * write it back... xxx yyy sendLine(sess, returnString); } else { String
	 * name=""; if (us == null) name = "?>"; else name = us.getName() + ">";
	 * history.add(name + message); broadcast(name + message); } return ; // avoid
	 * timeout, return nada... echoMsg; }
	 */

	// todo: fold case...
	//
	String jclPattern = "//";
	String jclSetnamePattern = "setname";
	String jclJoinPattern = "join";
	String jclRejoinPattern = "rejoin";
	String jclWhoAmIPattern = "whoami";
	String jclPoke = "poke";
	String jclResume = "resume";
	String jclSuperUser = "su";

	String jclIdentifierPattern = "[a-zA-Z0-9]+";
	Pattern jclRegex = Pattern.compile(jclPattern);
	Pattern jclSetnameRegex = Pattern.compile(jclSetnamePattern);
	Pattern jclJoinRegex = Pattern.compile(jclJoinPattern);
	Pattern jclRejoinRegex = Pattern.compile(jclRejoinPattern);
	Pattern jclWhoAmIRegex = Pattern.compile(jclWhoAmIPattern);
	Pattern jclSuperUserRegex = Pattern.compile(jclSuperUser);
	Pattern jclResumeRegex = Pattern.compile(jclResume);
	Pattern jclPokeRegex = Pattern.compile(jclPoke);
	
	Pattern jclIdentierRegex = Pattern.compile(jclIdentifierPattern);

	/*
	 * processJCL - should be parse JCL 
	 */
	// parseJCL command string into a command and type is JCLCommandType or JCLError
	// type
	void parseJCL(String commandString) {
		// scan for setname
		if (!isJCL(commandString)) {
			type = JCLType.JCLNotJCL;
			return;
		}
		type = JCLType.JCLError;
		Matcher m = jclSetnameRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLSetname;
			String sName = "user"; // default name to set to
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclSetnamePattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					sName = identifier.group(); // the pattern match
					// great. grabbed the name.
					NameValuePair p1 = new NameValuePair("name", sName);
					argv.add(p1);
					continue; // the command itself
				default:
					break;
				}
			}
			// us.setName(sName);
			return; // "//+Server setname:(" + sName + ")";
		}
		// scan for Rejoin (Note:scan for longer string first...
		m = jclRejoinRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLRejoin;
			String sName = "666"; // default player-id string
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclSetnamePattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					sName = identifier.group(); // the pattern match
					// great. grabbed the player-id to rejoine.
					NameValuePair p1 = new NameValuePair("name", sName);
					argv.add(p1);
					continue; // the command itself
				default:
					break;
				}
			}
			return; // "//+Rejoin game(" + sName + ")";
		} // ... if
			// scan for Join
		m = jclJoinRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLJoin;
			String sName = "game0"; // default name string
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					continue; // the command itself
				case 1:
					sName = identifier.group();
					break;
				}
			}
			return; // "//+Join game(" + sName + ")";
		}
		m = jclWhoAmIRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLWhoAmI;
			String sName = "game0"; // default name string
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclWhoAmIPattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					// args ignored...
					// sName = identifier.group();
					break;
				}
			}
			return; // "//+Join game(" + sName + ")";
		}
		m = jclSuperUserRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLSuperUser;
			String sName = "root"; // default name string
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclWhoAmIPattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					// args ignored for now...
					// todo: Add some damn security!
					// consider making the su add the time in hours +- 1...
					// sName = identifier.group();
					break;
				}
			}
			return;
		}
		// +new
		m = jclPokeRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLPoke;
			// String sName = "root"; // default name string
			// Matcher identifier = jclIdentierRegex.matcher(commandString);
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclWhoAmIPattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					// args ignored for now...
					// todo: Add some damn security!
					// consider making the su add the time in hours +- 1...
					// sName = identifier.group();
					break;
				}
			}
			return;
		}
		m = jclResumeRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			type = JCLType.JCLResume;
			// String sName = "root"; // default name string
			// Matcher identifier = jclIdentierRegex.matcher(commandString);
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					NameValuePair p0 = new NameValuePair(jclWhoAmIPattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					// args ignored for now...
					// todo: Add some damn security!
					// consider making the su add the time in hours +- 1...
					// sName = identifier.group();
					break;
				}
			}
			return;
		}
		// - new

		type = JCLType.JCLCommandNotRecognized;
		return; // "//JCL unimplemented command";

	}

	public boolean isJCL(String commandString) {
		// should make sure the first character is /
		if (commandString.charAt(0) == '/' &&
				commandString.charAt(1) == '/')
			return true;
		return false;
		/*
		 * Below matches anyting with // in it... 
		 * Not quite right...
		Matcher m = jclRegex.matcher(commandString);
		if (m.find(0)) {
			return true;
		}
		return false;
		*/
	}

	private boolean isSetname(String commandString) {
		
		Matcher m = jclSetnameRegex.matcher(commandString);
		if (m.find()) { // i.e. look for 'setname' then an identifier
			String sName = "user"; // default name string
			Matcher identifier = jclIdentierRegex.matcher(commandString);
			for (int i = 0; identifier.find(); i++) {
				switch (i) {
				case 0:
					type = JCLType.JCLSetname;
					NameValuePair p0=new NameValuePair(jclSetnamePattern, "");
					argv.add(p0);
					continue; // the command itself
				case 1:
					sName = identifier.group();	// the pattern match
					// great. grabbed the name.
					NameValuePair p1=new NameValuePair("name", sName);
					argv.add(p1);
					break;
				}
			} // for 
			} else {
				return false;	// Not setname
			}
			// create new nv pair, and make it the command parameter 0
			//us.setName(sName);
			return true;
			//return "//+Server setname:(" + sName + ")";
		}

	public UserJCLCommand(String commandString) {
		if (!isJCL(commandString)) {
			type = JCLType.JCLNotJCL; // this is the default anyway...
			return;
		} 
		parseJCL(commandString); 
		//xxx
		// Start work here... YYY
		// actually process... this is why you can separate process and parse
		/*
		 * try to parse as various commands; return if successful otherwise set type to
		 * be not recognized
		 */
		// ok, this shouldn't be called if processJCLString works... xxx
		/*
		if (isSetname(commandString))
			return;
		else 
			;
			*/
		// type = JCLType.JCLCommandNotRecognized;
		return;

	}

	public boolean isJCL() {
		if (type == JCLType.JCLNotJCL)
			return false;
		return true;
	}
}