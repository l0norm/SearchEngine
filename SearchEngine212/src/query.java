//class to handle the query 
public class query {
    
    LinkedList<Integer> result;
    private final String[] words;

    private LinkedStack<WordDocumentMapping> invertedIndexForQuery;
    

    private LinkedStack<String> operators;
    private LinkedList<String> postfix;


    public query(String query){
        words = query.split(" ");
        initializeQuery();
    }
    private void initializeQuery(){
        convertingToPostfix(words);
        processQuery();
        invertedIndexForQuery = new LinkedStack<>();
        result = new LinkedList<>();
    }

    public void convertingToPostfix(String[] words){//convert the query to postfix
        for(int i = 0; i < words.length; i++){//inserting the words to the postfix
            String word = words[i];
            if(word.equals("AND") || word.equals("OR")){//if the word is an operator
                operators.push(word);
            }else{
                postfix.insert(word);//inserting the word to the postfix
               
          }
            if (i%2==0) {//if the index is even
                String temp = operators.pop();
                if(temp.equals("AND")){//if the operator is AND ad it to the postfix cause it has higher precedence
                    postfix.insert(temp);
                }else{//if its other than and push it back to the stack
                    operators.push(temp);
                }
            }
        }
        while (!operators.empty()) {//push all the remaining operators to the postfix
           postfix.insert(operators.pop());
        }

    }


    public void processQuery(){
      
      while (!postfix.empty()) {
        if(postfix.retrieve().equals("AND") || postfix.retrieve().equals("OR")){//if its an operator process it 
           if(postfix.retrieve().equals("AND")){
            andProcess();
           }else{
            orProcess();
           }
           postfix.findNext();




        }else{//if its not an operator add it to the invertedindexforquery and its docIDs
            String word = postfix.retrieve();
            WordDocumentMapping newMapping = new WordDocumentMapping(word);
            Node<WordDocumentMapping> current = InvertedIndex.getHead();
            while (current != null) {//adding the docIDs to the newMapping
                if(current.data.word.equals(word)){
                    newMapping.docIDs = current.data.docIDs;
                    break;
                }
                current = current.next;
            }
            invertedIndexForQuery.push(newMapping);//insert the newMapping to the invertedindexforquery
            postfix.findNext();
        }

      }
      addToResult(invertedIndexForQuery);

    }




    public void andProcess(){//process the and operator a method in the linkedlist class
        WordDocumentMapping first = invertedIndexForQuery.pop();
        WordDocumentMapping second = invertedIndexForQuery.pop();

        first.docIDs.intersection(second.docIDs);
        
        if(first.docIDs.size() > 0){//if the result has docIDs
            invertedIndexForQuery.push(first);//push the result to the inverted index
        }
        
        

    }

    public void orProcess(){//process the or operator a method in the linkedlist class
        WordDocumentMapping first = invertedIndexForQuery.pop();
        WordDocumentMapping second = invertedIndexForQuery.pop();
        first.docIDs.union(second.docIDs);
        invertedIndexForQuery.push(first);

    }

    public void addToResult(LinkedStack<WordDocumentMapping> invertedIndexForQuery){//add the docIDs to the result ,,, to a linkedlist rather than a stack
        WordDocumentMapping current = invertedIndexForQuery.pop();
        while(!current.docIDs.empty()){
            result.insert(current.docIDs.retrieve());
            current.docIDs.findNext();
        }

    }

    
}
