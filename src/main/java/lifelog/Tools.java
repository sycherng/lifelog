package lifelog;
public class Tools {
        /** Given an Object, print the var name passed in, its type and contents.
         */
        public static void printTypeAndContent(Object obj, String name) {
                print(String.format("INSPECTING %1$s | TYPE %2$s | CONTENTS %3$s", name, obj.getClass().getName(),obj.toString()));
        }

        /** Takes a string and prints it. Simplifies the command for QoL while debugging. */
        public static void print(String str) {
                System.out.println(str);
        }	
}
