package ar.edu.ungs.tesina.micp;

import static org.junit.Assert.*;

import org.junit.Test;

import ar.edu.ungs.tesina.micp.example.MainApp;

public class IneqAvailableTest {

//	@Test
//	public void test() {
//		fail("Not yet implemented");
//	}
	
	@Test
	public void testWithoutInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -x".split(" ");
		MainApp.main(args);
		assertTrue(true);
	}
	
	@Test
	public void testWithPartitionedInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i2 -x".split(" ");
		MainApp.main(args);
		assertTrue(true);
	}
	
	@Test
	public void testWithThreePartitionedInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i3 -x".split(" ");
		MainApp.main(args);
		assertTrue(true);
	}
	
	@Test
	public void testWithKPartitionedInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i4 -x".split(" ");
		try {
			MainApp.main(args);
			assertTrue(false);
		} catch (Exception e)
		{
			assertTrue(true);
		}
	}	
	
	@Test
	public void testWithVertexCliqueInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i5 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithCliquePartitionedInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i6 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithSubCliqueInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i7 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithTwoColorSubCliqueInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i8 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithSemiTriangleInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i9 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithSemiDiamondInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i10 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithBoundingInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i11 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}
	
	@Test
	public void testWithReinforcedBoundingInequality() {
		String[] args = "-c /home/yoshknight/Documentos/ungs/tesina/doc/instancias/2010-02.txt -a 18 -s /home/yoshknight/Documentos/ungs/tesina/doc/instance-log/2010-02.I.sol_vertex_20181014 -t30 -p1 -i12 -x".split(" ");
		
		MainApp.main(args);
		assertTrue(true);
	
	}	

}
