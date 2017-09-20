
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class DecisionTree {
	
	DTNode root;
	int nodeCount;
	int leafCount;
	public DecisionTree(DTNode n){
		this.root = n;
		this.nodeCount = 0;
		this.leafCount = 0;
	}

	public static void main(String[] args) throws IOException{
		
		
		String stringFileTraining = args[0];
		String stringFileValidation = args[1];
		String stringFileTest = args[2];
		double pruningFactor = Double.parseDouble(args[3]);
		
		
		String[][] array = loadData(stringFileTraining);
		String[][] arrayValid = loadData(stringFileValidation);
		String[][] arrayTest = loadData(stringFileTest);
		
		
		List<Integer> rowZeros = new ArrayList<>();
		for(int i = 1; i < array.length; i++){
			rowZeros.add(i);
		}
		List<Integer> columnList = new LinkedList<>();
		
		
		DTNode x = nextBestNode(array,null,rowZeros);
		DecisionTree DT = new DecisionTree(x);
		DT.root = x;
		createDecisionTree(array,DT.root);
		//this function gives numbers to the nodes(level order)
		indexing(DT);
		
		System.out.println("Pre-Pruned Accuracy");
		System.out.println("--------------------");
		//printinf the summary
		printSummary(DT, array,arrayValid,arrayTest);
		
		int pruneNodes = (int)(pruningFactor * DT.nodeCount);
		//helper function for pruning the Tree
		helperPrune(DT, pruneNodes,array, arrayValid,arrayTest);	

		
	}
	
	//this function prints the summary of the decision Tree
	public static void printSummary(DecisionTree DT,String[][] array,String[][] arrayValid,String[][] arrayTest){
		
		double accuracy = findAccuracy(DT,array);
		double accuracy2 = findAccuracy(DT,arrayValid);
		double accuracy3 = findAccuracy(DT,arrayTest);
		DT.leafCount = findLeaf(DT.root);
		
		System.out.println("Number of training instances = " + (array.length - 1));
		System.out.println("Number of training attributes = " +(array[0].length - 1));
		System.out.println("Total number of nodes in the tree = " + DT.nodeCount);
		System.out.println("Number of leaf nodes in the tree = " + DT.leafCount);
		System.out.println("Accuracy of the model on the training dataset =" + accuracy + "\n" );
		
		
		System.out.println("Number of validation instances =" + (arrayValid.length-1));
		System.out.println("Number of validation attributes = " + (arrayValid[0].length - 1));
		System.out.println("Accuracy of the model on the validation dataset before pruning = " + accuracy2 + "\n");
		
		System.out.println("Number of testing instances = " + (arrayTest.length -1));
		System.out.println("Number of testing attributes = " + (arrayTest[0].length - 1));
		System.out.println("Accuracy of the model on the testing dataset = " + accuracy3 + "\n");
		
	}
	
	
	//helper function for pruning the Decision Tree
	//creates the a dummyTree and works on it till the accuracy is increased on the Validation_Set
	
	public static void helperPrune(DecisionTree DT,int n,String[][] array,String[][] arrayValid,String[][] arrayTest){
		int itr = 10;
		double maxAccuracy = findAccuracy(DT,arrayValid);
		double accuracy;
		while(itr > 0 ){
			DecisionTree fakeTree = new DecisionTree(null);
			fakeTree.root = dummyTree(DT.root);
			fakeTree.leafCount = DT.leafCount;
			fakeTree.nodeCount = DT.nodeCount;
			
			prune(fakeTree,n);
			accuracy  = findAccuracy(fakeTree,arrayValid);
			
			
			if(accuracy > maxAccuracy){
				//prints the summary after pruning the decision tree
				System.out.println("Post-Pruned Accuracy");
				System.out.println("--------------------");
				printSummary(fakeTree, array,arrayValid,arrayTest);
				
				//printing the tree
				System.out.println("If you want to print orginal DT type y");
				Scanner in = new Scanner(System.in);
				if(in.next().equals("y")){
					printTree(DT.root,"");
				}
				System.out.println("If you want to print Pruned DT type y");
				if(in.next().equals("y"))
					printTree(fakeTree.root,"");
				break;
			}
			itr--;
		}
		
	}
	
	//This function prunes the Decision Tree
	//uses rand function for generating the random numbers 
	// n is the number of nodes to be pruned
	public static void prune(DecisionTree DT,int n){
		
		Random rand = new Random();
		while(n>0){
			int count = DT.nodeCount;
			//considers only the second half of the nodes
			int num = rand.nextInt(count - ((1*count)/2) ) + (count/2 - 1);
			DTNode node = getNode(DT, num);
			node.left = null;
			node.right = null;
			node.isLeaf = true;
			if(node.posInstances >= node.negInstances)
				node.value = "1";
			else
				node.value = "0";
			// now after pruning the indexing of each node is updated
			indexing(DT);
			DT.nodeCount = DT.findNodes(DT.root);
			DT.leafCount = DT.findLeaf(DT.root);
			n--;
		}
	}
	// this function creates a dummy Tree
	public static DTNode dummyTree(DTNode node){
		if(node == null)
			return node;
		DTNode temp = new DTNode(node.name);
		temp.index = node.index;
		temp.isLeaf = node.isLeaf;
		temp.parent = node.parent;
		temp.number = node.number;
		temp.posInstances = node.posInstances;
		temp.negInstances = node.negInstances;
		temp.value = node.value;
		temp.left = dummyTree(node.left);
		temp.right = dummyTree(node.right);
		return temp;
	}

	//this function returns the node with the index(number) n
	public static DTNode getNode(DecisionTree DT,int n){
		//used queue for numbering level order
		Queue<DTNode> q = new LinkedList<DTNode>();
		q.add(DT.root);
		DTNode node;
		while(!q.isEmpty()){
			node = q.poll();
			if(node != null && !node.isLeaf){
				q.add(node.left);
				q.add(node.right);
				if(node.number == n)
					return node;
			}
		}
		return null;
	}
	
	//this function is used for indexing the Decision Tree only for internal nodes
	public static void indexing(DecisionTree DT){
		DTNode node = DT.root;
		Queue<DTNode> q = new LinkedList<DTNode>();
		int count = 1;
		q.add(node);
		DTNode temp;
		while(!q.isEmpty()){
			temp = q.poll();
			if(temp != null && !temp.isLeaf){
				temp.number = count++;
				q.add(temp.left);
				q.add(temp.right);
			}
		}
		DT.nodeCount = count -1;
	}
	
	//returns the total number of internal nodes
	public static int findNodes(DTNode n){
		if (n==null){
			return 0;		
		}
		else if(n.isLeaf){
			return 0;
		}
		return 1+findNodes(n.left)+findNodes(n.right);
		
	}
	
	//returns the total number of leaf nodes
	public static int findLeaf(DTNode n){
		if (n==null){
			return 0;
		}
		else if(n.isLeaf){
			return 1;
		}
		return findLeaf(n.left)+findLeaf(n.right);
	}
	
	//this function prints the Decision Tree as per the instructions(format)
	public static void printTree(DTNode root,String S){
		if(root == null){
			System.out.println(" : x");
			return;
		}
		if(root.isLeaf){
			if(root.posInstances == 0)
				System.out.print(" : 0");
			else if (root.negInstances == 0)
				System.out.print(" : 1");
			System.out.println();
		}
		else{
			System.out.println();
			System.out.print(S + root.name + " = 0 ");
			printTree(root.left,S+" |");
		    System.out.print(S+root.name+" = 1 ");
			printTree(root.right,S +" |");
		}
		
	}
	
	//this is a helper functionto  calculates the column list of a node and dosen't store it 
	public static void CalcColumnList(DTNode d,List<Integer> columnList){
		if(d==null){
			return;
		}
		columnList.add(d.index);
		CalcColumnList(d.parent,columnList);
	}

	//calculate the column list excluding the parent nodes
	public static List<Integer> FinalColumnList(List<Integer> columnList1, int Length){
		List<Integer> columnList2= new LinkedList<>();
		for(int i = 0; i < Length -1 ; i++)
			columnList2.add(i);
		for(Integer j: columnList1){
			columnList2.remove((Integer)j);
		}
		return columnList2;
		 
	}
	
	//this function develops the decision tree with the help of root node
	public static void createDecisionTree(String[][] array, DTNode node){
		
		if(node == null || node.negInstances == 0 || node.posInstances == 0)
			return;
		else{
		    List<Integer> columnList2 = new LinkedList<>();
		    columnList2=node.columnList;
		    node.left = nextBestNode(array, node, node.rowZeros);
		    node.right = nextBestNode(array,node, node.rowOnes);
			
		    createDecisionTree(array, node.left );
			createDecisionTree(array, node.right );
		
		}
	}
	
	//this function selects the next best node i.e the node with the maximum information gain
	public static DTNode nextBestNode(String[][] array,DTNode parent, List<Integer> rows){
		if(parent!=null&&parent.isLeaf){
			return null;
		}
		int posInstances = 0;
		int negInstances = 0;
		int leftInstances = 0;
		int rightInstances = 0;
		int leftposInstances = 0;
		int leftnegInstances = 0;
		int rightposInstances = 0;
		int rightnegInstances = 0;
		double minEntropy = 1;
		int realj =0 ;
		DTNode realnode = null;
		DTNode res = null;
		List<Integer> columnList = new LinkedList<>();
		CalcColumnList(parent,columnList);
		columnList= FinalColumnList(columnList,array[0].length);
		if(columnList.size()==0){
			List<Integer> rowZeros = new ArrayList<>();
			List<Integer> rowOnes = new ArrayList<>();
			List<String> c=new LinkedList<String>();
			posInstances = 0;
			negInstances = 0;
			DTNode node = new DTNode("Leaf");
			node.parent=parent;
			for(Integer i : rows){
			  if(array[i][array[0].length -1].equals("0"))
					{
						node.value = "0";
						negInstances++;
						
					}
				else{
					node.value = "1";
					posInstances++;
				}
			  if(array[i][parent.index].equals("0")){
				  rowZeros.add(i);
				  
			  }
			  else if(array[i][parent.index].equals("0")){
				  rowOnes.add(i);
				  
			  }
				
			}
			node.rowZeros=rowZeros;
			node.rowOnes=rowOnes;
			node.negInstances=negInstances;
			node.posInstances=posInstances;
			if(node.negInstances==0){
				node.value= "1";
			}
			else{
				node.value = "0";
			}
			node.isLeaf = true;
		    res=node;
		}
		else{
		for(Integer j : columnList){
				List<Integer> rowZeros = new ArrayList<>();
				List<Integer> rowOnes = new ArrayList<>();
				
				posInstances = 0;
				negInstances = 0;
				leftInstances = 0;
				rightInstances =0;
				leftposInstances = 0;
				leftnegInstances = 0;
				rightposInstances = 0;
				rightnegInstances = 0;
				if(rows == null)
					System.out.println("##############");
				else
				for(Integer i : rows){
					if(array[i][array[0].length -1].equals("0"))
						negInstances++;
					else
						posInstances++;
					
					
					if(array[i][j].equals("0")){
						leftInstances++;
						rowZeros.add(i);
						if(array[i][array[0].length -1].equals("0"))
							leftnegInstances++;
						else
							leftposInstances++;
					}
					else{
						rowOnes.add(i);
						rightInstances++;
						if(array[i][array[0].length -1].equals("0"))
							rightnegInstances++;
						else
							rightposInstances++;
					}
				}
				double leftEntropy = 1;
				double rightEntropy = 1;
				double parEntropy  = calculateEntropy(negInstances,posInstances);
				if(leftInstances == 0 && rightInstances == 0){
					leftEntropy = 1;
					rightEntropy = 1;
				}
				else if(leftInstances == 0 )	{
					leftEntropy = 1;
					rightEntropy = calculateEntropy(rightnegInstances,rightposInstances);
				}
				else if(rightInstances == 0){
					rightEntropy = 1;
					leftEntropy = calculateEntropy(leftnegInstances,leftposInstances);
				}
				else{
					leftEntropy = calculateEntropy(leftnegInstances,leftposInstances);
					rightEntropy = calculateEntropy(rightnegInstances,rightposInstances);
				}
				double avgEntropy = calculateAvgEntropy(leftInstances,leftEntropy,rightInstances,rightEntropy);
				
				if(avgEntropy <= minEntropy){
					
					minEntropy = avgEntropy;
					DTNode node = new DTNode(array[0][j]);
					node.posInstances = posInstances;
					node.negInstances = negInstances;
					node.leftInstances = leftInstances;
					node.rightInstances = rightInstances;
					node.rowOnes = rowOnes;
					node.rowZeros = rowZeros;
					node.parent= parent;
					node.index=j;
					node.isLeaf = false;
					realnode = node;
					realj = j;
					if(negInstances == 0 || posInstances == 0){
						if(node.negInstances==0){
							node.value= "1";
						}
						else{
							node.value = "0";
						}
						node.isLeaf=true;
						node.name="Leaf";
					}
					res = node;
				}
			}
		}
		return res;
	}
	
	//calculates the average Entropy of the node
	public static double calculateAvgEntropy(double lI,double lE,double rI,double rE){
		return ((lI/(lI+rI))*lE + (rI/(rI + lI))*rE);
	}
	
	//calculate the entropy of the node with paramaters negative instances and positive instances
	public static double calculateEntropy(double a,double b){
		
		double a1 = a / (a + b);
		double b1 = b/(a + b);
		double ans;
		if(a1==0.0 && b1 == 0.0){
			ans= -0.0;
		}
		else if(a1==0.0){
			ans = (-1)*(b1 ) *(Math.log10(b1)/Math.log10(2));
		}
		else if(b1==0.0){
			ans = (-1)*(a1 ) *(Math.log10(a1)/Math.log10(2));
		}
		else{
		ans = (-1)*(a1 ) *(Math.log10(a1)/Math.log10(2)) + (-1)*(b1 ) *(Math.log10(b1)/Math.log10(2));}
		return ans;
	}
	
	//helper function to calculate the accuracy
	public static double findAccuracy(DecisionTree DT, String[][] array){
		int count = 0;
		for(int i = 0 ; i < array.length; i++){
			if(isCorrectlyClassified(DT.root, array[i]))
				count++;
		}
		return ((double)count/((double)array.length-1))*100;
	}
	
	//returns true if the row is correctly classified in the given decisionTree
	public static boolean isCorrectlyClassified(DTNode node, String[] array){
		int i;
		
		while(node != null){
			i = node.index;
			if(node.isLeaf){
				if(array[array.length-1].equals(node.value)){
					//System.out.println("leaf");
					return true;
				}
				else
					return false;
			}
			if(array[i].equals("0")){
				node = node.left;
			}
			else {
				node = node.right;
			}
		}
		return false;
		
	}
	
	//function used to load data
	public static String[][] loadData(String stringFile) throws IOException{
		String nextLine;
		FileInputStream fis = new FileInputStream(stringFile);
		DataInputStream input = new DataInputStream(fis);
		nextLine = input.readLine();
		
		List<String[]> lines = new ArrayList<String[]>();
		while (nextLine  != null) {
		     lines.add(nextLine.split(","));
		     nextLine = input.readLine();
		}
		String[][] array = new String[lines.size()][0];
		lines.toArray(array);
		return array;
	}
	
	
}
		