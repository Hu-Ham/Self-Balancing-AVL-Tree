
/* Algorithms and Data Structures Project 3: Self-Balancing AVL Tree:
 * Hugh Hamilton, Spring 2020
 * AVL Tree with O(log2(n)) insertion, deletion, and look up speed
 * Insert: look for duplicates when inserting. No two elements should be the same
 * Delete – If the user selects Delete you should print out an in-order
	traversal of the tree to show the user, which elements are in the tree to be
	deleted. Make sure to handle cases in which the user tries to delete
	elements that do not exist or delete form an empty tree.
- Look Up - This selection should return the element selected and the level
	that element was at in the tree. (e.g. the root will be at level 0 and
	children of the root will be at level 1) Make sure to handle cases in which
	the user tries to look up elements that do not exist or look up elements in
	an empty tree.
- Print – You should print the tree using both an in-order and a pre-order
	traversal of the tree.
	
	This tree inserts values in O(log(n)) time, and then checks every time that a value has been inserted, 
	also in log(n) time, to make sure that the tree is balanced. If it is not balanced, the rotation class 
	is called, which will then perform one of four types of rotations, depending on the circumstances. 
	
	Deleting nodes operates in much the same way. First we examine whether the node with the specified value
	exists, and then all references to that node are removed. Deletion must be broken down into one of three
	sub-cases: deletion of a leaf node, deletion of a node with one child, and deletion of a node with two 
	children. 
	Deletion of a leaf node is extremely straightforward. All that is required is to eliminate all references 
	to it from its parent, and then update the height of all nodes immediately above the now-deleted leaf.
	Then, the balance factor (height of left subtree minus height of right subtree) of all nodes above is
	assessed. If the balance factor indicates that the node is unbalanced (heights differ by more than one),
	then rotations are performed.
	Deletion of a node with a single child is more difficult. This requires moving the single child to that
	node's previous position in its parent, and then updating the heights of all nodes above, and then 
	similarly checking for the balance factor and rotating nodes as necessary.
	The most annoying deletion is the deletion of a node with two children. In this case, the node that is
	the closest numerically to the deleted node must in turn be deleted and placed in the first deleted node's
	position. This node is found by either finding the rightmost node on the first node's left subtree,
	(which I do in this program), or the leftmost node in the first node's right subtree. This "closest node"
	fortunately is always either a single-child node or a leaf node- this is true for the simple reason that
	no rightmost or leftmost node can have a right or left child, or else it would not be the right or 
	leftmost. Similarly, after this is performed, we must check for imbalance, and correct it. 
	
	Searching for nodes is straightforward and occurs in log(n) time. Starting at the root, we compare the
	root to the desired value, and compare to left or right children recursively until we either find a
	matching node, or have reached a leaf node. By using a pointer to this node if it exists, we may know its
	attributes, such as its height value.
	
	Traversing the binary search tree and printing out each value is done in two ways, "in order" and "pre-
	order". An "in order" traversal requires recursion towards the leftmost node of the entire tree, and 
	the placement of its value in a stack. That node's parent, and then its right sibling are added, and 
	the recursion terminates after finishing placing every value from the tree in a stack, sorted from smallest
	to largest. 
	
	Traversing the binary search tree "pre order" requires first accessing the data of the root, and then
	all of its left children, before then the right nodes. This is an example of a "depth first" search.
	
	Printing here is done by printing the root, and then printing each subsequent level by returning all the 
	children nodes of the tree as penetrated to a certain depth. We print until the "height" - ie longest
	individual path- of the tree has been fully traversed. 
 */
import java.util.*;
import java.lang.Math; //used for the max() function in height calculation
public class BalancedTreeDriver {
	public Node nodePointer; //pointer to some given node- value overwritten as necessary.
	public Node unbalancedNode; //pointer to specifically an unbalanced node- also overwritten as necessary
	public Node root; //pointer to root node
	public Stack<Integer> inOrderStack; //stack to hold all node values in order
	public Stack<Integer> preOrderStack; //stack to hold all node value in pre-order
	//public int numberOfNodes = 0; //add one to this for every node added- it being less than 40 
	//is necessary for print-out of AVL tree
	public static void main(String[] args) {
		System.out.println("Welcome to Hugh Hamilton's AVL Tree Program!");
		System.out.println("To begin: "
				+ "please enter a number to add to the binary search tree.");
		System.out.println("Press enter to add it, and stop adding numbers by typing");
		System.out.println("a letter or other character and pressing enter.");
		
		//after some other value, splash menu asking for:
		//either if-switches or a case statement
		//Insert:
		//Delete:
		//Look up:
		//Print:
		BalancedTreeDriver btd = new BalancedTreeDriver();
		//accepts input for the tree prior to options menu
		btd.enterValues();
		//cheery thank-you send off 
		System.out.println("Thank you for using my binary tree! ");
		System.out.println("-Hugh Hamilton, 2020");

		//btd.executeTree();
	}
	public void enterValues() {
		Scanner treeScanner = new Scanner(System.in);
		System.out.println("Enter value: ");
		try {
			int value = treeScanner.nextInt();
			addNode(value);
			//check tree for balance
			//while(!checkBalance()) {//and if not balanced, call balancing method upon unbalanced node
			//	rotate(unbalancedNode); //continually apply it until checkBalance returns true
			//}
			//repeat:
			enterValues();
		}
		catch(InputMismatchException e) { //This permits an empty tree to be entered, through invalid input
			System.out.println("All values now added.");
			System.out.println();
			displayMenu();
			//Display menu
		}
	}
	//Called after some (or no) initial values are added to the tree and subsequently height-balanced
	public void displayMenu() {
		Scanner menuScanner = new Scanner(System.in);
		System.out.println("Choose one of the following by typing in a number and pressing enter: ");
		System.out.println("1: Insert values to the tree");
		System.out.println("2: Delete values from the tree");
		System.out.println("3: Look up a specific value in the tree ");
		System.out.println("4: Print an in-order traversal of the tree ");
		System.out.println("5: Print a pre-order traversal of the tree ");
		System.out.println("6: Print out the tree ");
		System.out.println("7: End program ");

		//Switch statement to call methods that will activate these different options
		try {
			switch(menuScanner.nextInt()) {
			case 1:
				enterValues();
				break;
			case 2:
				deleteValues();
				break;
			case 3:
				lookUpValue();
				break;
			case 4:
				inOrder();
				break;
			case 5:
				printPreOrder();
				break;
			case 6:
				printTree();
				break;
			case 7:
				return;
			default:
				menuError();
			}
		}
		catch(InputMismatchException e) {
			menuError();
		}
	}
	//To be called for invalid input that is either the wrong type of integer, or not an integer.
	private void menuError() {
		System.out.println("Invalid input. Please enter a number from 1 - 5. ");
		System.out.println();
		displayMenu();
	}
	//Called by menu, allows user deletion of values
	public void deleteValues() {
		Scanner deleteScanner = new Scanner(System.in);
		System.out.println("Enter invalid input in order to stop deleting values.");
		System.out.println("Enter value to delete: ");
		try {
			int value = deleteScanner.nextInt();
			removeNode(value); 
			deleteValues();
		}
		catch(InputMismatchException e) {
			System.out.println("Invalid input, returning to menu.");
			System.out.println();
			displayMenu();
		}
	}
	//Called by menu- determines if value exists, and then displays height iff it exists.
	public void lookUpValue() {
		Scanner lookupScanner = new Scanner(System.in);
		System.out.println("Please enter an integer value to look up: ");
		try {
			int value = lookupScanner.nextInt();
			if(root == null) {
				System.out.println("Sorry, this tree is empty.");
				System.out.println("If you would like, you may add values from the menu.");
				System.out.println();

			}
			else if(nodePresent(value)) { //the nodePresent method sets the nodePointer to the node iff it exists
				System.out.println("The node with value " + nodePointer.value + " exists.");
				System.out.println("This node has a height of " + nodePointer.height + ".");
				System.out.println();
			}
			else {
				System.out.println("There is no node with value " + nodePointer.value + ".");
				System.out.println("If you would like, you may add this value from the menu.");
				System.out.println();
			}
			displayMenu();
		}
		catch(InputMismatchException e) {
			System.out.println("Invalid input, returning to menu");
			System.out.println();
		}
	}
	//Called by enterValues() - has empty tree exception and otherwise calls recursive node adder
	public void addNode(int nodeVal) {
		if(root == null) { //case: empty tree and adding first node
			root = new Node(nodeVal);
			root.setHeight(0);
			return;
		}
		//case: non empty tree
		addNode(nodeVal, root);
	}
	//Recursive node adder:
	//takes an integer value for a node, and then adds that node to the tree
	//the check and subsequent tree balancing will be executed after
	private void addNode(int nodeVal, Node parent) {
		if(parent.value == nodeVal) {
			System.out.println("Error: node with value " + nodeVal + " already exists.");
			return;
		}
		if(parent.value > nodeVal) { //put node to left of parent
			if(parent.leftNode == null) {
				setLeftChild(parent, nodePointer = new Node(nodeVal));
				nodePointer.setHeight(0);
				updateHeights(nodePointer);
				checkBalance(nodePointer.parent);
				//System.out.println("Node with value " + nodeVal + " has been successfully added.");
				return;
			}
			addNode(nodeVal, parent.leftNode); //case it is defined
			
		}
		if(parent.value < nodeVal) { //put node to right of parent
			if(parent.rightNode == null) {
				setRightChild(parent, nodePointer = new Node(nodeVal));
				nodePointer.setHeight(0);
				updateHeights(nodePointer);
				checkBalance(nodePointer.parent);
				//System.out.println("Node with value " + nodeVal + " has been successfully added.");
				return;
			}
			addNode(nodeVal, parent.rightNode); //right node is defined
		}	
	}
	//Called by deleteValues(), passed the value to delete in particular
	//if the value does not exist, error output passed. Otherwise, recursive removeFromTree method called
	public void removeNode(int removeValue) {
		if(root == null) {
			System.out.println("Error: empty tree.");
			return;
		}
		
		if(!nodePresent(removeValue)) {
			System.out.println("Error: node with value " + removeValue + " does not exist.");
			return;
		}
		//case- node has been removed
		//run a method that will remove the node
		removeFromTree(nodePointer);
		//System.out.println("Node with value " + removeValue + " has been successfully removed.");
		
	}
	//Called by the above- passed the global variable nodePointer, which always has the value 
	//of the node that was last searched for in the nodePresent method. This now divides into one of three
	//cases.
	private void removeFromTree(Node removeThisNode) {
		//three cases:
		//one child (l/r), two children (root or not), no children (leaf or lonely root)
		//case: leaf node
		if(removeThisNode.leftNode == null && removeThisNode.rightNode == null) {
			if(removeThisNode.value == root.value) { //single node tree
				root = null;
				return;
			}
			//leaf node of tree size >1
			if(removeThisNode.parent.leftNode == removeThisNode) { //if this is a left hand node
				removeThisNode.parent.leftNode = null;
				updateHeights(removeThisNode); //update heights through method that ascends to root
				//increasing node heights as necessary (maximum of left and right height)
				checkBalance(removeThisNode); //balance checked here from the same node- if a node is out
				//of balance, then one of four rotations will be performed.
				removeThisNode = null;
				return;
			}
			if(removeThisNode.parent.rightNode == removeThisNode) { //if this is a right hand node
				removeThisNode.parent.rightNode = null;
				updateHeights(removeThisNode);
				checkBalance(removeThisNode);
				removeThisNode = null;
				return;
			}
		}
		//case: single left child
		//check for if it is root
		//equivalent to if(rightNode == null && leftNode != null), because right and left being null
		//are the precondition of the above
		//*
		Node right1 = removeThisNode.rightNode;
		Node left1 = removeThisNode.leftNode;
		
		if((right1 == null) ^ (left1 == null)) {
			//System.out.println("Single child delete: " + removeThisNode.value);
			//System.out.println(nodePointer.leftNode.value + " and " + nodePointer.rightNode.value);

			Node parent = removeThisNode.parent;
			Node singleChild;
			if(removeThisNode.rightNode == null)
				singleChild = removeThisNode.leftNode;
			else
				singleChild = removeThisNode.rightNode;
			//three subcases: node is right child, node is left child, node is root
			if(removeThisNode == root) {
				root = singleChild;
				root.setParent(null);
				singleChild.parent = null;
				return;
			}
			if(parent.leftNode == removeThisNode) {
				setLeftChild(parent, singleChild);
				updateHeights(removeThisNode);
				checkBalance(removeThisNode.parent);
				return;
			}
			if(parent.rightNode == removeThisNode) {
				setRightChild(parent, singleChild);
				updateHeights(removeThisNode);
				checkBalance(removeThisNode.parent);
				return;
			}
		}//*/
		
		//System.out.println("Two child delete: " + removeThisNode.value);

		//case: two children
		//find the closest (numerically) node to the node to delete
		//the closest node always has one or zero children- because it is the "right-most" node left of
		//the parent node that we are deleting. For it to be the "right-most", it must have no more right children.
		Node closestNode = closestNode(removeThisNode);

		//Node closerNode = closestNode;
		removeFromTree(closestNode);

		Node left = removeThisNode.leftNode;
		Node right = removeThisNode.rightNode;
		setRightChild(closestNode, right);

		if(left != null )
			setLeftChild(closestNode, left); 
		//the reason for the above is in the eventuality that "closestnode" is a left leaf node of
		//the removed node- meaning that left might be defined as null after removing closestNode
		if(removeThisNode == root) {
			root = closestNode;
			root.setParent(null);
			return;
		}
		else if(removeThisNode == removeThisNode.parent.leftNode) {
			removeThisNode.parent.leftNode = closestNode;
			closestNode.setParent(removeThisNode.parent);
			updateHeights(closestNode.rightNode);
			checkBalance(closestNode.rightNode);

			return;
		}
		else {
			removeThisNode.parent.rightNode = closestNode;
			closestNode.setParent(removeThisNode.parent);
			updateHeights(closestNode.rightNode);
			checkBalance(closestNode.rightNode);
			return;
		}
	}
	//Called by removeFromTree above in order to delete a two-child node. It find the "numerically closest"
	//node the node that must be deleted, to put in its place. In this case, it is the rightmost value
	//of the left subtree.
	private Node closestNode(Node parent) { 
		Node closestNode = parent.leftNode; //initialize as left hand node
		if(closestNode.rightNode == null) {
			return closestNode;
		}
		//Otherwise- while loop to descend the right hand values
		while(closestNode.rightNode != null) {
			closestNode = closestNode.rightNode;
		}
		return closestNode;
	}
	//simplifies things to only require a single variable to be passed into method declarations.
	//NodePresent is called by deletion methods to determine whether the node to be deleted exists, and is
	//called by lookUpValue() to determine whether it exists. It returns a true or false, but iff it returns
	//true, it will also set the nodePointer node pointer to the value of that node, for use in either deletion
	//or to acquire its height in the lookUpNode() method.
	public boolean nodePresent(int nodeValue) {
		return nodePresent(root, nodeValue);
	}
	private boolean nodePresent(Node parent, int nodeValue) { //for use in recursion
	
		if(parent.value == nodeValue) {
			nodePointer = parent;
			return true;
		}
		//move left
		if(parent.value > nodeValue && parent.leftNode != null) {
			return nodePresent(parent.leftNode, nodeValue);
		}
		//move right
		if(parent.value < nodeValue && parent.rightNode != null) {
			return nodePresent(parent.rightNode, nodeValue);
		}
		/* The following cases:
		 * in all cases: nodevalue != parent value
		 * case: nodeValue is less than parent, but there is no left hand child of parent
		 * case: nodeValue is greater than parent, but there is no left hand child of parent
		 * 
		 */
		return false; //base case
			
	}
	//This is called several times when entering values into the tree or removing them.
	//It traverses the tree from where a value is entered or removed after the nodes' heights are re-adjusted.
	//If a node is unbalanced, the rotations are called.
	public void checkBalance(Node noo) { //checks balance and then rotates accordingly
		//we climb the tree from noo to the root
		
		if(balanceFactor(noo) > 1 || balanceFactor(noo) < -1) { //if noo is unbalanced
			rotate(noo);
			return;
		}
		if(noo == root) { //once the climb has finished without throwing unbalanced-error, base case
			return; 
		}
		
		checkBalance(noo.parent); //recur
	}
	//The balance factor of a node is the height of its left subtree minus the height of its right subtree.
	//This value can be used to determine whether a node is unbalanced- if the balance factor falls outside
	//the range -1, 1 inclusive.
	//This is only called by checkBalance.
	private int balanceFactor(Node noo) {
		int leftHeight = nodeHeight(noo.leftNode);
		int rightHeight = nodeHeight(noo.rightNode);
		return (leftHeight - rightHeight);
	}
	//A method to expedite setting a left child. This sets the pointer of the parent leftwards, and of the
	//child parent-wards. Called by many methods.
	public void setLeftChild(Node parent, Node child) {
		parent.setLeftNode(child);
		child.setParent(parent);
	}
	//See above
	public void setRightChild(Node parent, Node child) {
		parent.setRightNode(child);
		child.setParent(parent);
	}
	//Called to re-adjust node heights after either the introduction of a new node or deletion of a node.
	//Done by finding the maximum of the heights of each left and right subtree plus one. Subtrees also include 
	//the empty pointers attached to leaf or single-child nodes.
	public void updateHeights(Node nodePtr) { //climb up the tree to root and adjust all heights
 		//set the node heights above to +1
		Node pointer2 = nodePtr;
		while(pointer2.parent != null) { //stop after root, which has null parent
			Node nParent = pointer2.parent;
			nParent.setHeight(Math.max(nodeHeight(nParent.leftNode), nodeHeight(nParent.rightNode)) + 1);
			pointer2 = nParent;
		}
	}
	//Very useful method that allows assessing height of both null pointers and pointers to sub-trees.
	public int nodeHeight(Node noo) { //method for getting height of both null and non null nodes=
		if(noo == null)
			return -1;
		return noo.height;
	}
	//Very complicated method that in hindsight should have been four methods. 
	//Called when a node is unbalanced. It triggers one of four cases, described below.
	public void rotate(Node unbalanced) {
		Node left = unbalanced.leftNode;
		Node right = unbalanced.rightNode;
		Node prent = unbalanced.parent;
		//analyze the unbalanced node to see if it's one of the following four cases:
		//left-left heavy, right-right heavy, left-right heavy, or right-leftheavy
		//check for case 1: left-left heavy. This is when the unbalanced node is left heavy, and its left child is also
		//left heavy. As a result, we will use a "right rotation".
		/*
		 * Initial shape:
		 * 			A
		 * 		B
		 *  C		(b right child iff exists)
		 *  Post- rotation:
		 *  		B
		 *  	C		A
		 *  		(former b right child, A left child)
		 */
		if(nodeHeight(left) > nodeHeight(right) && nodeHeight(left.leftNode) > nodeHeight(left.rightNode)) {
			//the fact that the left hand side is checked first is essential towards avoiding a null pointer
			//error here.
			//right rotate
			//despite the redunancy of having multiple pointers to the same place, the code is much more readable with the 
			//below. Refer to the diagram above. 
			Node nodeA = unbalanced;
			Node nodeB = left;
			Node nodeC = left.leftNode; //not used, but kept here for maximum clarity
			if(nodeB.rightNode != null) { //set left child of unbalanced node to right child of left, iff it exists
				setLeftChild(nodeA, nodeB.rightNode);
				nodeB.setRightNode(null); //remove pointer!
			}
			setRightChild(nodeB, nodeA); //rotate unbalanced node to right position of left
			if(nodeA.leftNode == nodeB)
				nodeA.setLeftNode(null);
			
			//case: root
			if(nodeA == root) {
				root = nodeB;
				nodeB.setParent(null);
			}
			else { //find connection to parent from unbalanced node
				nodeB.setParent(prent);
				if (prent.leftNode == nodeA) 
					setLeftChild(prent, nodeB);
				else 
					setRightChild(prent, nodeB);
			}

			rotateHeightUpdate(nodeA);
			rotateHeightUpdate(nodeC);
			return;
		}
		//check for case 2: right-right heavy. Similarly, avoiding a null pointer error in condition by only 
		//including fields that can exist if the first condition in the && is met. Requires "left rotation".
		//Rotation is mirror image of case 1 in behavior and appearance.
		/*
		 * Initial shape:
		 * 						A
		 * 								B
		 * (b left child iff exists) 			C		
		 *  
		 *  Post- rotation:
		 *  			B
		 *  	A				C
		 *  (former b left child, A right child)
		 */
		else if(nodeHeight(right) > nodeHeight(left) && nodeHeight(right.rightNode) > nodeHeight(right.leftNode)) {
			Node nodeA = unbalanced;
			Node nodeB = unbalanced.rightNode;
			Node nodeC = unbalanced.rightNode.rightNode;
			//"left rotate":
			if(nodeB.leftNode != null) {
				setRightChild(nodeA, nodeB.leftNode);
				nodeB.setLeftNode(null); //avoid double pointer to same node
			}
			setLeftChild(nodeB, nodeA); //rotate unbalanced node to left position of its right hand child
			if(nodeA.rightNode == nodeB)
				nodeA.setRightNode(null);
			
			//case: root
			if(nodeA == root) {
				root = nodeB;
				root.setParent(null);
			}//otherwise, set the right node's parent as unbalanced's parent
			else {
				nodeB.setParent(prent);
				if (prent.leftNode == nodeA) 
					setLeftChild(prent, nodeB);
				else 
					setRightChild(prent, nodeB);
			}
			rotateHeightUpdate(nodeA);
			rotateHeightUpdate(nodeC);
			return;
		}
		//case 3: left right heavy. Heavier on left side, and then right.
		/*
		 * Initial shape: 
		 * 			A
		 * 		B
		 * 			C
		 * Rotate to left-left heavy:
		 * 			A
		 * 		C
		 *   B
		 * Then call rotate for case 1 to activate
		 */
		else if(nodeHeight(left) > nodeHeight(right) && nodeHeight(left.rightNode) > nodeHeight(left.leftNode)) {
			//for more clear language: pointers:
			Node nodeA = unbalanced;
			Node nodeB = unbalanced.leftNode; //same as "left"
			Node nodeC = unbalanced.leftNode.rightNode;
			setLeftChild(nodeA, nodeC);
			setLeftChild(nodeC, nodeB);
			nodeB.setRightNode(null); //remove pointer to its parent (circular tree is bad)
			updateHeights(nodeB);
			rotateHeightUpdate(nodeB);
			rotate(unbalanced); //now active case one bc this will be left left heavy
			return;
		}
		//case 4: right left heavy. Heavier on right side, then left.
		//Behavior is mirror image of case 3:
				/*
				 * Initial shape: 
				 * 	A
				 * 		B
				 * 	C
				 * Rotate to right-right heavy:
				 * 	A
				 * 		C
				 *   		B
				 * Then call rotate for case 2 to activate
				 */
		else if(nodeHeight(right) > nodeHeight(left) && nodeHeight(right.leftNode) > nodeHeight(right.rightNode)) {
			Node nodeA = unbalanced;
			Node nodeB = right;
			Node nodeC = right.leftNode;
			setRightChild(nodeA, nodeC);
			setRightChild(nodeC, nodeB);
			nodeB.setRightNode(null);
			rotateHeightUpdate(nodeB);
			rotate(unbalanced);
			return;
		}
		
	}
	//After rotation, sometimes heights are altered in an annoying way that the updateHeight method won't fix
	//This will fix that. It will make sure that leaf nodes that were previously not leaf nodes will set 
	//their heights to zero, allowing the recursive CORRECT updating of heights to all nodes above in ancestry.
	public void rotateHeightUpdate(Node checkHeight) {
		if(checkHeight.leftNode == null && checkHeight.rightNode == null) {
			checkHeight.setHeight(0);
			updateHeights(checkHeight);
			return;
		}
		if(checkHeight.leftNode != null) {
			updateHeights(checkHeight.leftNode);
		}
		else {
			updateHeights(checkHeight.rightNode);
		}
	}
	//Called by the menu. It will print out the entire tree from smallest to largest value.
	public void inOrder() { //call the inOrder recursive method
		inOrderStack = new Stack<Integer>(); //initialize the stack of values in order
		inOrder(root); //call the recursive method with the root node
		System.out.println("The following is all of the nodes from the tree in order: ");
		int i = 0; //index variable
		for(int n : inOrderStack) {
			if(i%10 == 0) //skip lines for each modulus of ten, to keep from creating one enormous line
				System.out.println();
			System.out.print(n + " "); //separate each value with a space
			i++;
		}
		System.out.println();
		System.out.println();
	}
	//Recursive, called by the above
	private void inOrder(Node parent) { //inpired by code seen in textbook page 668:
		if(parent.leftNode != null)
			inOrder(parent.leftNode);
		inOrderStack.push(parent.value);	
		if(parent.rightNode != null)
			inOrder(parent.rightNode);
	}
	//Like the above, but pre order
	public void printPreOrder() { //also inspired by code seen in textbook page 668:
		preOrderStack = new Stack<Integer>();
		printPreOrder(root);
		System.out.println("The following is all of the nodes from the tree in pre-order ordering: ");
		int i = 0; //index variable
		for(int n : preOrderStack) {
			if(i%10 == 0) //skip lines for each modulus of ten, to keep from creating one enormous line
				System.out.println();
			System.out.print(n + " "); //separate each value with a space
			i++;
		}
		System.out.println();
		System.out.println();
		displayMenu();
	}
	private void printPreOrder(Node parent) {
		preOrderStack.push(parent.value);
		if(parent.leftNode != null )
			printPreOrder(parent.leftNode);
		if(parent.rightNode != null)
			printPreOrder(parent.rightNode);
	}
	//Called by the menu. Print the entire tree, for a basic look at the connection between the nodes.
	//Not beautiful, but a good way to immediately see the structure and determine if the balance is correct.
	public void printTree() {
		int i = 0;
		while(i < root.height + 1) {
			System.out.println(printTree(root, i));
			i++;
		}
		displayMenu();
	}//base case- add node to either empty tree or continue
	
	private String printTree(Node parent, int i) {
		//if i ==0 and node is defined, return node
		if(parent == null)
			return "null";
		if(i == 0)
			return parent.value + "";
		//base case
		return printTree(parent.leftNode, i - 1) +" " + printTree(parent.rightNode, i - 1);
	}
}
//Node class. Called very frequently for obvious reasons.
class Node {
	/*
	 * Key/value
	 * Left pointer
	 * Right pointer
	 * Parent pointer
	 * Height value
	 */
	int value;
	int height;
	Node leftNode;
	Node rightNode;
	Node parent;
	Node(int value){
		this.value = value;
	}
	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}
	public void setRightNode(Node rightNode) {
		this.rightNode = rightNode;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
