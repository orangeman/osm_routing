package net.pragyah.scalgorithms.heaps

import scala.collection.mutable.{HashSet,HashMap}

trait Heap[A] {
  def insert(a:A)
  def extractMin() : Option[A]
  def delete(a:A)
  def minimum : Option[A]
  def decreaseKey(a:A,k:A) 
  def empty : Boolean
  
  def += (a:A) = insert(a)
  def -= (a:A) = delete(a)
  def ++= (l:List[A]) = l.foreach(+= _)
  def --= (l:List[A]) = l.foreach(-= _)
}


class FibonacciHeap[A <% Ordered[A]](val aMin:A) extends Heap[A] {

  type Node[A] = FibonacciHeap.Node[A]

  var n = 0;
  var min:Node[A] = null
  // a map to get direct access to the nodes given the key .... decerease key and delete key would pass on the keys and not the nodes
  val keyNodeMap = new HashMap[A,List[Node[A]]]()
  
  def minimum():Option[A] = {
   if(min != null) Some(min.key)
   else None
  }
  
  def empty = min == null
  
  // insert a new element ... cost O(1)  (actual and amortized)
  def insert(a:A) = {
    val x = new Node[A](a)
    x.degree = 0
    x.parent = null
    x.children = Nil
    x.left = x
    x.right = x
    x.mark = false
    if(!keyNodeMap.contains(a)) keyNodeMap += a -> Nil
    keyNodeMap += a -> (x::keyNodeMap(a))
    addToRootlist(x)
    n = n + 1
  }

  //extract the minimum value .... cost O(D(n)) Amortized... were D(n) is the max degree which generally is log n
  def extractMin() : Option[A] = {
    if(min == null)
      return None 
    
    val z = min
    
    z.children.foreach(addToRootlist) //add each child of z to the root list 
    //remove it from the linked list it exists in ... 
    z.right.left = z.left
    z.left.right = z.right
    
    if(z.right == z){
       min = null
    }  //this means there were no children and there is nothing else in the root list
    else {
      min = z.right; 
      consolidate() 
    } 
    
    n = n-1 // decrement as one value is being popped out
    
    if(keyNodeMap(z.key).size == 1) //remove from the key node map
      keyNodeMap.removeKey(z.key)
    else{
      keyNodeMap += z.key ->  keyNodeMap(z.key).remove(_ == z)
    }
    Some(z.key)
  }

  def delete(a:A) = {
    assume(keyNodeMap.get(a) != None)
    decreaseKey(a,aMin)
    extractMin()
  }

  //consolidate after extract min .... cost O(D(n)) ... were D(n) is the max degree which generally is log n
  private def consolidate() = {
    // create an auxiliary array .. where temporarily stored node has a degree equal to the index in the Array A 
    val A = new Array[Node[A]](2*Math.log(n).toInt+1) // Max degree is O(lg n) .. CLRS pg 479 ... 
    val Amap = new HashSet[Node[A]]() // temporary map to keep a track of all the nodes that are added to array A
    
    //loop through each node in the root list of H
    var w = min
    
    while(!Amap.contains(w)){ // this tells if the next node w is already in the array A .. if yes .. then we have completed the cycle 
      var x = w
      var d = x.degree
      while(A(d) != null){
        var y = A(d) //another node (already discovered) with the same degree as x
        if(x.key > y.key){ //exchange x <--> y 
          var xy = x
          x = y
          y = xy
        }
        link(y,x)
        A(d) = null // set Ad to null as the degree of x would increase and y is no more in the root list
        Amap -= (y,x)
        d = d + 1 //increment d to lookup pre-discovered node with a degree equivalent to x's (d+1)
      }
      A(d) = x
      Amap += x
      w = x.right
    }    
    min = null // empty the root list

    //now reconstruct the root list from the array ... 
    A.filter(_ != null).foreach(addToRootlist)
    
  }
  
  // link two nodes .. making y a child of x
  private def link(y:Node[A],x:Node[A]) = {
    //remove y from where it was  
    y.left.right = y.right
    y.right.left = y.left
    //make y a child of x, incrementing degree of x (done inside addChild method)
    x.addChild(y)
  }
  

  def decreaseKey(a:A,k:A) = {
    val aList = keyNodeMap(a)
    
    assume(aList != null && aList.size != 0 )
    val x = aList.head
    
    x.key = k //replace the key
    
    if(!keyNodeMap.contains(k)) keyNodeMap += k -> Nil
    keyNodeMap += k -> (x::keyNodeMap(k))
    
    if(aList.tail.size == 0)
      keyNodeMap -= a
    else
      keyNodeMap += a -> aList.tail   
    
    val y = x.parent
    
    if(y != null && x.key < y.key){
      cut(x,y)
      cascading_cut(y)
    }
    if(x.key < min.key) min = x  //if smallest .. set as min

  }
  
  private def cut(x:Node[A],y:Node[A]) ={
    //remove x from the child list of y and add it to the root list of the heap
    addToRootlist(y.removeChild(x)) 
    x.mark = false;
  }

  private def cascading_cut(y:Node[A]):Unit = {
    val z = y.parent
    if(z != null){
      y.mark match{
	      case false => y.mark = true
	      case true => cut(y,z); cascading_cut(z)
      }
    }
  }
  
  
  private def addToRootlist(x:Node[A]) = {

	x.parent = null
	x.left = x
	x.right = x
	if(min == null)
       min = x
	else{
	  x.left = min.left;
      x.right = min;
	  min.left.right = x;
	  min.left = x
	  if(x.key < min.key) min = x
	}
  }
  
  override def toString() : String = {
    var str = "HEAP"
    if(min == null){
      str = str.concat(" EMPTY "); return str
    }
    var c = min
    str =str.concat("\n-").concat(c.toTreeString("\t",1))
    c = min.right
    while(c != min){
      str =str.concat("\n-").concat(c.toTreeString("\t",1))
      c = c.right
    }
    
    str
    
  }
  

}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


object FibonacciHeap{
  
  def union[A <% Ordered[A]](H1:FibonacciHeap[A],H2:FibonacciHeap[A]) : FibonacciHeap[A] = {
    val H = new FibonacciHeap[A](H1.aMin)
    H.min = H1.min

    val h2MinL = H2.min.left
    val hMinR = H.min.right
    
    H.min.right = H2.min
    H2.min.left = H.min
 
    h2MinL.right = hMinR
    hMinR.left = h2MinL
    
    if(H1.min ==null || (H2.min != null && H2.min.key < H1.min.key))
      H.min = H2.min
    H
  }

  	/*
	 * NODE
	 */
	
	private[FibonacciHeap] object Node{
	  def apply[A](key:A) = new Node(key)
	}
	
	private[FibonacciHeap] class Node[A](var key:A){
	   var degree = 0
	   var parent:Node[A] = null
	   var children:List[Node[A]] = List()
	   var left:Node[A] = null
	   var right:Node[A] = null
	   var mark = false
	   
	   override def toString = key.toString
	   
	   def addChild(y:Node[A]):Node[A] = {
	         y.left = y
		     y.right = y
	     
	     if(!children.isEmpty){
		     val x = children.head
	         y.left = x.left
		     y.right = x
	         x.left.right = y
		     x.left = y
	     } 
	       
	     children = y :: children
	     
	     y.parent = this
	     y.mark = false
	     degree = degree + 1
	     y
	   }
	   
	   def removeChild(x:Node[A]):Node[A] = {
	     assume(children.contains(x))
	
	     x.left.right = x.right
	     x.right.left = x.left
	     x.parent = null
	     degree = degree - 1
	     children = children.remove(_ == x)
	     x
	   }
	
	 
	  def toTreeString(indent:String,indentLevel:Int) : String = {
	    var indentStr = indent
	    for(i <- 1 to indentLevel) indentStr = indentStr.concat(indent)
	    
	    var str = indentStr.concat(key.toString)
	    children.foreach(child => str = str.concat("\n").concat(child.toTreeString(indent,indentLevel+1)))
	    str
	  }
	  
	}
    
}

