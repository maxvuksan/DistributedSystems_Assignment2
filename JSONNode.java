import java.util.HashMap;

public class JSONNode {

    JSONNode(){

        childNodes = new HashMap<String, JSONNode>();
    }


    public static JSONNode Parse(String raw){

        return JSONNode.RecursiveParse(raw, new JSONNode());
    }

    public String Stringify() {

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
    
        boolean first = true; // to manage comma placement
        for (String key : childNodes.keySet()) {
            if (!first) {
                jsonBuilder.append(","); // No space before comma for compact format
            }
            first = false;
    
            JSONNode childNode = childNodes.get(key);
            jsonBuilder.append("\"").append(key).append("\":");
    
            // If childNode has children, recursively call ToString
            if (childNode.childNodes.isEmpty()) {
                // If it's a leaf node, append its raw value
                jsonBuilder.append("\"").append(childNode.valueRaw).append("\"");
            } else {
                jsonBuilder.append(childNode.Stringify()); // Recursively stringify the child node
            }
        }
    
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    
    private static JSONNode RecursiveParse(String raw, JSONNode parentNode){

        int index = 0;

        // iterate over each character
        while(index < raw.length()){

            if (raw.charAt(index) == '{') {

                int endIndex = FindClosingBrace(raw, index);

                if (endIndex != -1) {
                    
                    // each key : value pair
                    String[] tokenPairs = raw.substring(index + 1, endIndex).split(",");

                    for(int i = 0; i < tokenPairs.length; i++){
                        HandleTokenPair(tokenPairs[i], parentNode);
                    }
                    //objects.add(raw.substring(index, endIndex + 1));
                    index = endIndex + 1;
                } 
                else {
                    break;
                }

            } 
            else {
                index++;
            }

        }

        return parentNode;
    }

    private static void HandleTokenPair(String token, JSONNode parentNode) {
        
        String[] keyValue = token.split(":", 2); // Split only into 2 parts
        if (keyValue.length < 2) {
            return; // Not a valid key-value pair
        }
    
        String key = keyValue[0].trim();
        String value = keyValue[1].trim();
    
        // Clean up key
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
    
        JSONNode childNode = new JSONNode();
    
        // Check for nested JSON object
        if (value.startsWith("{")) {
            // Parse nested object
            int closingBraceIndex = FindClosingBrace(value, 0);

            if (closingBraceIndex != -1) {
                RecursiveParse(value.substring(0, closingBraceIndex + 1), childNode);
                childNode.valueRaw = ""; // It has children, raw value is empty
            } 
            else {
                System.err.println("Error: unmatched braces in JSON.");
                return;
            }
        } else {
            // Leaf node
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            childNode.valueRaw = value; // Set raw value
        }
    
        parentNode.childNodes.put(key, childNode);
    }


    // Method to find the matching closing brace for a given opening brace

    private static int FindClosingBrace(String raw, int start) {

        int braceCount = 0;

        for (int i = start; i < raw.length(); i++) {

            if (raw.charAt(i) == '{') {
                braceCount++;
            } 
            else if (raw.charAt(i) == '}') {

                braceCount--;
                
                if (braceCount == 0) {
                    return i;
                }
            }
        }
        return -1; // No matching brace found
    }


    /*
     *  @returns a child to a node 
     *  allows nested JSON to be accessed
     * 
     *  e.g.
     * 
     *  JSONNode a;
     * 
     *  a.Get("b").Get("c").AsString()
     * 
     *  {
     *      b: {
     *              c: "Hello"
     *         }
     *  }
     */
    public JSONNode Get(String key){

        return childNodes.get(key);
    }

    public String AsString(){
        return valueRaw;
    }

    public int AsInt(){
        return Integer.parseInt(valueRaw);
    }

    public float AsFloat(){
        return Float.parseFloat(valueRaw);
    }

    private HashMap<String, JSONNode> childNodes;

    // unparsed value, parses on access
    private String valueRaw;
    
}
