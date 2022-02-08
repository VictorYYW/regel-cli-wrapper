package edu.stanford.nlp.sempre.overnight.test;

import edu.stanford.nlp.sempre.overnight.SimpleWorld;
import fig.basic.*;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Test simple world from overnight framework.
 * Creates a small database using SimpleWorld,
 * and does sanity checks on the induced knowledge graph
 * @author Yushi Wang
 */
public class SimpleWorldTest {
  @Test public void externalWorldTest() {
    SimpleWorld.opts.domain = "external";
    SimpleWorld.opts.dbPath = "lib/data/overnight/test/unittest.db";
    SimpleWorld.opts.verbose = 1;
    SimpleWorld.recreateWorld();

    assertEquals(SimpleWorld.sizeofDB(), 12);
  }

  public static void main(String[] args) {
    new SimpleWorldTest().externalWorldTest();
  }
}
