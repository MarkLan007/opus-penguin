package server.ws;

import java.util.Scanner;
import java.util.Vector;

public class UserJCLCommand {
	public enum JCLType {
		JCLNotJCL, // String is not a JCL command
		JCLError, // JCL command but malformed
		JCLCommandNotRecognized, // Command is not recognized
		JCLCommandNotImplemented, // Command is not implemented
		JCLSetname, // set the name attached to user
		JCLJoin, // Join, Rejoin games
		JCLLs,
		JCLNew,		// create a new game
		JCLStart,
		JCLRejoin, 
		JCLSu, 		// SuperUser was JCLSuperUser...
		JCLRefresh,
		JCLResend, JCLReset,
		JCLNewdeal, JCLMisdeal,
		JCLShuffle,
		JCLStatus,
		JCLPeek,	// peek at a hand
		JCLScore,
		JCLPoke, JCLResume, 	// poke or resume the server
		JCLWhoAmI, JCLWho,
		JCLPrevious;
		
		static JCLType dontParseList[] = { 
				JCLNotJCL, // String is not a JCL command
				JCLError, // JCL command but malformed
				JCLCommandNotRecognized, // Command is not recognized
				JCLCommandNotImplemented, // Command is not implemented
		};

	};
	JCLType type;
	static JCLType[] enumList=JCLType.values();
	static JCLType dontParse[] = { 
			JCLType.JCLNotJCL, // String is not a JCL command
			JCLType.JCLError, // JCL command but malformed
			JCLType.JCLCommandNotRecognized, // Command is not recognized
			JCLType.JCLCommandNotImplemented, // Command is not implemented
	};

	public class Command {
		JCLType type;
		String commandName="";
		Command(JCLType t, String name) {
			type = t;
			commandName = name;		
		}
		Command() {
			
		}
	}

	static String[] commandStrings = null;
	//static Command commandList[] = null;
	static Vector<Command> commandList=
			new Vector<Command>();

	static boolean isInit = false;

	String encode(Command c) {
		return c.commandName + "(?)";
	}
	
	boolean dontParse(JCLType t) {
		for (JCLType notCommand : JCLType.dontParseList)
			if (t == notCommand)
				return true;			
		return false;
	}
	
	void sort() {
		int i = 0, j = 0;
		for (i = 0; i < commandList.size(); i++)
			for (j = i + 1; j < commandList.size(); j++)
				// so longer and heavier strings are first...
				if (commandList.elementAt(j).commandName.compareTo(
						commandList.elementAt(i).commandName) > 0) {
					// swap
					Command temp = commandList.elementAt(j);
					commandList.set(j, commandList.elementAt(i));
					commandList.set(i,  temp);
				}
	}

	void buildCommandStructure() {
		/*
		 * build structures with the enum value and the string to actually parse for
		 */
		Command c = null;
		for (JCLType t : JCLType.values()) {
			String s = t.name();
			s = s.substring(3);
			s = s.toLowerCase();
			if (dontParse(t))
				continue;
			c = new Command(t, s);
			commandList.add(c);
		}

		// Now sort the list, so that all substrings are after
		// anything contained by it i.e. //str1 is before //str
		// .. used to match the longest string and short circuit failed searches
		sort();
		// Vector<NameValuePair> argv=new Vector<NameValuePair>(3,1);

	}

	public Vector<Command> getCommandList() {
		return commandList;
	}

	/*
	 * Argc and Argv
	 */
	public class NameValuePair {
		String name="";
		String value="";
		NameValuePair(String sName, String sValue) {
			name = sName;
			value = sValue;
		}
		public String getValue() { return value;}
	}

	Vector<NameValuePair> argv=new Vector<NameValuePair>(3,1);
	/*
	 * Note that arguments are actually zero based as an array
	 *  but user expects argv convention that argv[0] is command name
	 */
	String getName(int index) {
		
		if (index > argc())
			return "Param out of bounds";
		else
			return argv.get(index).name;
	}
	
	/*
	 * getValue - should return the arg at index, not monkeyed with
	 * argv[0] is the command, and there is an entry at a[0] to reflect that
	 */
	String getValue(int index) {
		if (index > argc())	// Uh-oh
			return "param out of bounds";
		return argv.get(index).value;
	}
	/*
	 * argc() - wierd, huh? argc is a function. 
	 */
	public int argc() {
		return argv.size();	// + 1? Huh?
	}

	void createArgv(String arg0) {
		argv.add(new NameValuePair(arg0, arg0));
	}
	
	void addArg(String arg) {
		/*
		 * ToDO: if there is an equal, break into
		 * name and value
		 */
		argv.add(new NameValuePair(arg, arg));
	}
	
	static boolean isBlank(char c) {
		if (c == ' ' || c == '\t' 
				|| c == '\n' || c == ',')
			return true;
		return false;
	}

	static boolean isAlphanum(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= 'A' && c <= 'Z')
			return true;
		if (c >= '0' && c <= '9')
			return true;
		return false;
	}
	
	static boolean oddBall(char c) {
		if (!isAlphanumPlus(c) && !isBlank(c))
			return true;
		return false;
	}

	// true if alphanum or = sign, etc that get parsed into command blobs
	static boolean isAlphanumPlus(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= 'A' && c <= 'Z')
			return true;
		if (c >= '0' && c <= '9')
			return true;
		if (c == '=' || c == '+' || c == '-')
			return true;
		return false;
	}

	/*
	 * if s is a command return the command
	 * assumes: commandList is sorted properly...
	 * match the longest string and short circuit failed searches
	 */
	Command match(String s) {
		int i = 0, j = 0;
		s = s.toLowerCase();
		s = s.substring(2);
		String verb = "";
		// span blanks
		for (i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (isBlank(c))
				;
			else
				break;
		}
		for (; i < s.length(); i++) {
			char c = s.charAt(i);
			if (isAlphanum(c))
				;
			else
				break;
			verb += c;
		}	// i is index of last alphanumeric character in s

		for (Command c : commandList) {
			// see if the command is matched.
			// matched if first n characters match and string is
			// now looking at whitespace
			// ** note ** no longer accepts just the substring; requires exact match
			int comp = verb.compareTo(c.commandName);
			if (comp == 0) {
				// it's a match. Set type.
				this.type = c.type;
				System.out.println(verb + ".type->" + this.type);
				// Now build argc and argv
				// get blobs between whitespace
				// TODO: if there is an equal sign, parse-up
				createArgv(verb);
				j = i + 1;
				do {
					for (; j < s.length(); j++)
						if (isBlank(s.charAt(j)))
							;
						else
							break;
					String blob = "";
					for (; j < s.length(); j++)
						if (isAlphanumPlus(s.charAt(j)))
							blob += s.charAt(j);
						else
							break;
					if (j < s.length() && oddBall(s.charAt(j)))
						j++;
					if (blob.length() > 0)
						addArg(blob);
				} while (j < s.length());	// i.e. get multiple args
				return c; // got command, got args return the command.
			} else if (comp > 0) {
				// verb is lexically higher than this string
				// so it will never find it because everything
				// after this is lower...
				return null;
			} else { // comp < 0 v
						// verb is lexically lower than this string
						// keep on searching for a heart of gold.
				continue;
			}
		}
		return null;

	}

	UserJCLCommand(String commandstring) {
		this();
		if (!isJCL(commandstring)) 
			return;
		Command cmd=match(commandstring);
		if (cmd != null) {
			// this.type = cmd.type;
			// build argc and argv
			return;
		}
		type = JCLType.JCLError;
	}
	
	UserJCLCommand() {
		if (!isInit) {
			buildCommandStructure();
			isInit = true;
		}
		type = JCLType.JCLNotJCL;
	}
	
	/*
	 * it's jcl if len >= than 2 chars and starts with //
	 */
	private boolean isJCL(String commandString) {
		if (commandString.length() < 2)
			return false;
		if (commandString.charAt(0) != '/' || commandString.charAt(1) != '/')
			return false;
		return true;
	}

	/*
	 * read-eval-print loop for testing...
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		jclttyhandler();
	}
	
	static public void echo(String s) {
		System.out.printf("echo> %s\n", s);
	}

	static void jclttyhandler() /* throws IOException */ {
		String input = "";
		int i = 0;
		// BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.printf("JCL test console v1.0 quit to exit, help for help\n");
		System.out.flush();
		Scanner scanner = new Scanner(System.in);
		while (input != null) {
			NewUserJCLCommand jcl = new NewUserJCLCommand();
			System.out.printf("%d. ", i);

			input = scanner.nextLine();

			echo(input);
			// System.out.flush();
			if (input == null) {
				echo("null. quitting...");
				break;
			} else if (input.startsWith("quit")) {
				echo("quitting...");
				break;
			} else if (input.startsWith("help")) {
				echo("no help yet...");
			} else if (input.startsWith("dump")) {
				//Vector<Command> v=jcl.getCommandList();
				for (int j=0; j<commandList.size(); j++) {
					String s=commandList.elementAt(j).commandName;
					JCLType t=commandList.elementAt(j).type;
					System.out.println(s + "(" + t + ")" + " args...");
				}
			} else {				
				jcl = new NewUserJCLCommand(input);
				System.out.println("Type->" + jcl.type);
				System.out.println("Parameters...");
				for (int j=0; j<jcl.argc(); j++)
					System.out.println("Arg" + j 
							+ ">" +jcl.getName(j) + "=" + jcl.getValue(j));
			}
			i++;
		}
		scanner.close();
	} // ttyHandler

} // Class...
