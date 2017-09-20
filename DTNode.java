import java.util.List;

public class DTNode {
	String name;  //name of the node  i.e column name
	boolean isLeaf; // if true node is leaf
	String value; //node class either 0 or 1 for leaf Nodes
	int posInstances, negInstances,leftInstances,rightInstances; 
	int index,number;
	DTNode parent; // parent of the node
	DTNode left,right;
	List<Integer> rowZeros,rowOnes; //Zero list and one's list of the rows
	List<Integer> columnList; // columns to be checked (i.e this list excludes the parent nodes)
	public  DTNode(String name){
		this.name = name;
		this.isLeaf = false;
		this.right = null;
		this.left= null;
		this.parent=null;
	}
}
