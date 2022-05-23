import java.util.*;
import java.util.List;

/**
 * Library for graph analysis
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author Chukwuka, CS 10, Winter 2022, modifying for the purpose of completing Problem Set 4
 * @author Anish, CS 10, Winter 2022, modifying for the purpose of completing Problem Set 4
 *
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)

	 * @param g        graph to walk on
	 * @param start    initial vertex (assumed to be in graph)
	 * @param steps    max number of steps

	 * @return    a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 *            null if start isn't in graph
	 */
	public static <V, E> List<V> randomWalk (Graph<V, E> g, V start, int steps) {
		// TODO: your code here

		// returns null if the start vertex is not in the graph
		if (!g.hasVertex(start)) return null;

		// initialize & instantiate a new ArrayList called pathList
		ArrayList<V> pathList = new ArrayList<>();

		// add the inputted starting vertex to the pathList
		pathList.add(start);

		// set the current pointer equal to the starting vertex
		V curr = start;

		// set an integer i equal to zero, for use in the following while loop
		int i = 0;
		while(i < steps) {

			// generates a list of neighbours for the vertex under consideration
			ArrayList<V> listOfNeighbours = new ArrayList<>();

			// take all the neighbors for the current vertex
			// add it to the new list of neighbors for the current vertex
			g.outNeighbors(curr).forEach(listOfNeighbours::add);

			// chooses a random neighbour
			int randomNeighbor = (int) (0 + Math.random() * listOfNeighbours.size());

			// sets current equal to the random neighbor
			curr = listOfNeighbours.get(randomNeighbor);

			// adds the random neighbor to the path
			pathList.add(curr);

			// returns the list as is if curr has no neighbours
			if (g.outDegree(curr) == 0) return pathList;

			// increment i
			i += 1;
		}

		// returns the resulting list of vertices
		return pathList;
	}

	/**
	 * Orders vertices in decreasing order by their in-degree

	 * @param g       graph

	 * @return    list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 * (inDegree – the number of edges going into a node, is its inDegree)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		// TODO: your code here

		// initialize & instantiate a new ArrayList
		ArrayList<V> verticesSorted = new ArrayList<>();

		// for each vertex in the graph, add it to the verticesSorted list
		g.vertices().forEach(verticesSorted::add);

		// sort each vertex within the list, by passing in an anonymous function as a comparator
		// the anonymous function takes in two vertices and returns the difference between their inDegrees
		verticesSorted.sort((vertexOne, vertexTwo) -> g.inDegree(vertexTwo) - g.inDegree(vertexOne));

		// return the sorted list of vertices by their inDegree
		return verticesSorted;
	}

	/**
	 * BFS to find shortest path tree for a current center of the universe. Return a path tree as a Graph.

	 * @param g			A graph
	 * @param source	A starting point
	 * @param <V>		The data type of vertices
	 * @param <E>		The data type of edge labels

	 * @return 			A tree (a graph with a root node) where all vertices directly point to the center of the universe
	 * 					or indirectly do so, through the shortest path.
	 */

	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {

		// initialize & instantiate a new Graph
		Graph<V, E> bfsGraphTree = new AdjacencyMapGraph<>();

		// insert a new vertex into the graph, which will be the current center of the universe
		bfsGraphTree.insertVertex(source);

		// initialize & instantiate a new Set for visited Vertices
		Set<V> visited = new HashSet<>();

		// initialize & instantiate a new Queue for the bfs neighbor checking
		Queue<V> queue = new LinkedList<>();

		// add the center of the universe source vertex to the queue to check its neighbors
		queue.add(source);

		// add the center of the universe source vertex to the visited list to avoid double–checking
		visited.add(source);

		// while the queue is not empty
		while (!queue.isEmpty()) {

			// pop the vertex which is first in the queue
			V newElement = queue.remove();


			// for every out neighbor of the popped vertex
			for (V v: g.outNeighbors(newElement)) {


				// if it hasn't already been visited
				if (!visited.contains(v)) {

					// add it to the visited list
					visited.add(v);

					// add it to the queue
					queue.add(v);

					// insert this out neighbor
					bfsGraphTree.insertVertex(v);

					// insert an edge from this out neighbor back to the vertex which was popped
					bfsGraphTree.insertDirected(v, newElement, g.getLabel(newElement, v));


				}
			}
		}

		// return the tree (a graph with a root), with vertices all pointing (directly or indirectly) towards the source vertex
		return bfsGraphTree;
	}

	/**
	 * Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe.
	 * @param tree		Shortest path tree (graph with a center of universe as root) from any vertex to the center of the universe
	 * @param v			A given vertex
	 * @param <V>		The data type of vertices
	 * @param <E>		The data type of edge labels
	 * @return			Shortest path from the given vertex to the center of the universe
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {

		// initialize & instantiate a new ArrayList
		List<V> pathToCenter = new ArrayList<V>();

		// add the given vertex
		pathToCenter.add(v);

		// set the current vertex equal to the given vertex
		V curr = v;

		// while the outDegree for the current point is greater than 0 (as long as the current vertex is pointing to something)
		while (tree.outDegree(curr) > 0) {
			// for each out neighbor of the current point
			for (V eachVertex: tree.outNeighbors(curr)) {

				// add the out neighbor to the path list
				pathToCenter.add(eachVertex);

				// set the current value equal to the out neighbor
				curr = eachVertex;
			}
		}
		// return the list of vertices from the given vertex to the center of the universe
		return pathToCenter;
	}


	/**
	 * Given a graph and a subgraph (here shortest path tree), determine which vertices are in the graph but not the
	 * subgraph (here, not reached by BFS).
	 * @param graph			the given graph with all the vertices
	 * @param subgraph		the subgraph which bfs has been run on
	 * @param <V>			The data type of vertices
	 * @param <E>			The data type of edge labels
	 * @return				return a set of vertices which are in the graph, but not in the subgraph
	 */

	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {

		// initialize & instantiate a new Set, which will hold all the missing vertices
		Set<V> missingVertexSet = new HashSet<>();

		// initialize & instantiate a new Set, which will hold all the points in the graph
		Set<V> graphSet = new HashSet<>();

		// loop over all the vertices in the graph, and add them to the set
		graph.vertices().forEach(graphSet::add);

		// initialize & instantiate a new Set, which will hold all the points in the subgraph
		Set<V> subgraphSet = new HashSet<>();

		// loop over all the vertices in the subgraph, and add them to the set
		subgraph.vertices().forEach(subgraphSet::add);

		// for each vertex within the Graph set
		for (V eachVertex: graphSet) {

			// if the Subgraph set does not contain the vertex
			if (!subgraphSet.contains(eachVertex)) {

				// add this vertex to the missing vertex set
				missingVertexSet.add(eachVertex);
			}
		}

		// return the set of missing vertices
		return missingVertexSet;
	}

	/**
	 * Find the average distance-from-root in a shortest path tree.
	 * Note: do this without enumerating all the paths! Hint: think tree recursion...
	 * @param tree		Shortest path tree
	 * @param root		Root vertex
	 * @param <V>		The data type of vertices
	 * @param <E>		The data type of edge labels
	 * @return			return a double, which is the average distance from the root in a shortest path tree
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {

		// pass the parameters (and an initial length of zero) to the helper function
		// additionally, divide the result of this helper function by the number of vertices minus 1,
		// in order to calculate average separation
		return (averageSeparationHelper(tree, root, 0) / (tree.numVertices()-1));
	}

	/**
	 * Helper function which is called by average separation
	 * @param tree		Shortest path tree
	 * @param root		Root vertex
	 * @param length	Zero is passed in
	 * @param <V>		The data type of vertices
	 * @param <E>		The data type of edge labels
	 * @return			return a double
	 */
	public static <V,E> double averageSeparationHelper(Graph<V,E> tree, V root, double length) {

		// set the averageDistanceHelper equal to length, initially 0
		double averageDistanceHelper = length;

		// for each vertex in the root's inNeighbor's
		for (V eachVertex: tree.inNeighbors(root)) {

			// add to the averageDistanceHelper, by recursively calling the averageSeparationHelper
			// and adding one to the length in the parameter
			averageDistanceHelper += averageSeparationHelper(tree, eachVertex, length + 1);
		}

		// return the averageDistanceHelper, which is the total distance between all the vertices
		return averageDistanceHelper;
	}
}