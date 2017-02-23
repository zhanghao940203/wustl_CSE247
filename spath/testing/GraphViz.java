package spath.testing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import spath.graphs.DirectedGraph;
import spath.graphs.Edge;
import spath.graphs.Vertex;

import java.awt.SystemColor;

/**
 * 
 * @author timhuber
 *
 */
public class GraphViz<K extends Comparable<K>> {
	private DirectedSparseMultigraph<Vertex, Edge> g;
	private DirectedGraph inputGraph;
	private List<Edge> shortestPath;
	private List<Edge> studentShortestPath;
	private Map<Edge, K> weights;
	private Vertex start;
	private Vertex end;
	
	/**
	 * 
	 * @param graph input graph for visualization
	 * @param startID id of the first vertex of the shortest path being tested
	 * @param endID id of the last vertex of the shortest path being tested
	 * @param shortestPath A linkedlist containing the sequential edges of the
	 * correct shortest path
	 * @param yourShortestPath The shortest path generated by students' algorithm
	 */
	public GraphViz(DirectedGraph graph, Map<Edge, K> weights, Vertex start, Vertex end, 
			List<Edge> shortestPath, List<Edge> yourShortestPath) {
		this.g = new DirectedSparseMultigraph<Vertex, Edge>();
		this.inputGraph = graph;
		this.shortestPath = shortestPath;
		this.studentShortestPath = yourShortestPath;
		this.weights = weights;
	}

	public void print() {
		for (Vertex v : inputGraph.vertices()) {
			for (Edge e : v.edgesFrom()) {				
				g.addEdge(e, e.from, e.to, EdgeType.DIRECTED);
			}
		}
		
		//Other nice looking available layouts include:
		//FRLayout(), KKLayout()
		//Change the line below if you prefer one of those graph representations
		Layout<Vertex, Edge> layout = new CircleLayout<Vertex,Edge>(g);
		layout.setSize(new Dimension(700,700)); 

		BasicVisualizationServer<Vertex, Edge> vv = new BasicVisualizationServer<Vertex,Edge>(layout);
		vv.setBounds(0, 0, 750, 750);

		//label edges with their weights
		Transformer<Edge,String> edgeWeightLabeller = new Transformer<Edge,String>() {
			@Override
			public String transform(Edge arg0) {
				return "" + weights.get(arg0);
			}
		};
		
		//make the edge labels bigger
		Transformer<Edge, Font> bigFont = new Transformer<Edge, Font>() {
			@Override
			public Font transform(Edge arg0) {
				return new Font(Font.SANS_SERIF, Font.BOLD, 15);
			}
		};
		
		//label vertices according to their toString(), unless they are the start and
		//end of the shortest path, in which case we label them "start" and "end" resp.
		Transformer<Vertex,String> vertexLabeller = new Transformer<Vertex,String>() {
			@Override
			public String transform(Vertex arg0) {
				if(arg0 == start)
					return "start";
				else if(arg0 == end)
					return "end";
				else
					return arg0.toString();
			}	
		};

		//Paint the start and end vertices green, the rest red
		Transformer<Vertex,Paint> vertexPaint = new Transformer<Vertex,Paint>() {
			@Override
			public Paint transform(Vertex arg0) {
				if(arg0 == start)
					return Color.GREEN;
				else if(arg0 == end)
					return Color.RED;
				else
					return Color.LIGHT_GRAY;
			}
		};

		//increase size of vertices
		Transformer<Vertex,Shape> vertexSize = new Transformer<Vertex,Shape>(){
			@Override
			public Shape transform(Vertex v){
				Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);

				return circle;
			}
		};

		//Set any edges part of the actual, or student returned shortest path to solid
		//and think. Set remaining edges thing and dotted.
		float dash[] = {10.0f};
		final Stroke dashStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		final Stroke thickStroke = new BasicStroke(3.0f);

		Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
			@Override
			public Stroke transform(Edge arg0) {
				if(shortestPath.contains(arg0) || studentShortestPath.contains(arg0))
					return thickStroke;
				return dashStroke;
			}

		};
		
		//if a student shortest path was provided, color those arrows blue
		//if an edge is in the actual shortest path, color it black
		//if an edge is extraneous, color it gray
		if(studentShortestPath != null) {
			Transformer<Edge, Paint> edgePaint = new Transformer<Edge, Paint>() {
				@Override
				public Paint transform(Edge arg0) {
					if(shortestPath.contains(arg0)) {
						if(studentShortestPath.contains(arg0)) {
							return Color.GREEN;
						}
						return Color.BLUE;
					}
					if(studentShortestPath.contains(arg0)) {
						return Color.RED;
					}
					return Color.GRAY;
				}
				
			};
			vv.getRenderContext().setArrowFillPaintTransformer(edgePaint);
			vv.getRenderContext().setArrowDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);	
		}

		//use all of the transformers created above
		vv.setPreferredSize(new Dimension(750,750)); 
		vv.getRenderContext().setVertexLabelTransformer(vertexLabeller);
		vv.getRenderContext().setEdgeLabelTransformer(edgeWeightLabeller);
		vv.getRenderContext().setEdgeFontTransformer(bigFont);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);;
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		vv.getRenderContext().setLabelOffset(2);

		
		JPanel legend = genLegend();
		
		//generate the graph in swing;
		JFrame frame = new JFrame("Graph Visualization");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.window);
		panel.setBounds(0, 0, 1100, 750);
		panel.setLayout(null);
		panel.add(vv);
		panel.add(legend); 
		frame.setContentPane(panel);
		frame.setSize(1100, 750);
		frame.setVisible(true);       
	}
	
	//generates a legend for the graph
	private JPanel genLegend() {
		JPanel panel = new JPanel();
		panel.setBounds(750, 0, 350, 250);
		panel.setBackground(SystemColor.window);
		panel.setLayout(null);
		
		JLabel lblYourPathOverlaps = new JLabel("Your path overlaps with the correct path:");
		lblYourPathOverlaps.setBounds(6, 0, 262, 29);
		panel.add(lblYourPathOverlaps);
		
		JPanel greenLine = new JPanel();
		greenLine.setBackground(Color.GREEN);
		greenLine.setForeground(Color.GREEN);
		greenLine.setBounds(25, 30, 142, 10);
		panel.add(greenLine);
		
		JLabel lblEdgeReturnedBy = new JLabel("Edge returned by your algorithm that is incorrect:");
		lblEdgeReturnedBy.setBounds(6, 52, 320, 29);
		panel.add(lblEdgeReturnedBy);
		
		JPanel redLine = new JPanel();
		redLine.setBackground(Color.RED);
		redLine.setBounds(25, 81, 142, 10);
		panel.add(redLine);
		
		JLabel lblCorrectShortestPath = new JLabel("Correct shortest path edge:");
		lblCorrectShortestPath.setBounds(6, 103, 180, 29);
		panel.add(lblCorrectShortestPath);
		
		JPanel blueLine = new JPanel();
		blueLine.setBackground(Color.BLUE);
		blueLine.setBounds(25, 131, 142, 10);
		panel.add(blueLine);
		
		JLabel lblUninvolvedEdge = new JLabel("Uninvolved Edge:");
		lblUninvolvedEdge.setBounds(6, 154, 180, 29);
		panel.add(lblUninvolvedEdge);
		
		JPanel dash1 = new JPanel();
		dash1.setForeground(Color.WHITE);
		dash1.setBackground(SystemColor.window);
		dash1.setBounds(69, 179, 15, 10);
		panel.add(dash1);
		dash1.setLayout(null);
		
		JPanel dash2 = new JPanel();
		dash2.setLayout(null);
		dash2.setForeground(Color.WHITE);
		dash2.setBackground(SystemColor.window);
		dash2.setBounds(103, 179, 15, 10);
		panel.add(dash2);
		
		JPanel dash3 = new JPanel();
		dash3.setForeground(Color.WHITE);
		dash3.setBackground(SystemColor.window);
		dash3.setBounds(35, 179, 15, 10);
		panel.add(dash3);
		dash3.setLayout(null);
		
		JPanel dash4 = new JPanel();
		dash4.setLayout(null);
		dash4.setForeground(Color.WHITE);
		dash4.setBackground(SystemColor.window);
		dash4.setBounds(137, 179, 15, 10);
		panel.add(dash4);
		
		JPanel grayLine = new JPanel();
		grayLine.setBackground(Color.GRAY);
		grayLine.setBounds(25, 179, 142, 10);
		panel.add(grayLine);
		grayLine.setLayout(null);
		
		return panel;
	}
}
