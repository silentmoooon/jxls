package net.sf.jxls;

import net.sf.jxls.tag.Block;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Leonid Vysochyn
 */
public class BlockTest   {
    public void testEquals(){
        Block b1 = new Block(1, (short) 2, 3, (short)4);
        Block b2 = new Block(1, (short) 2, 3, (short)4);
        assertTrue( b1.equals( b2 ));
        b2 = new Block(0, (short) 2, 3, (short)4);
        assertFalse( b1.equals( b2 ));
        b2 = new Block(1, (short) 3, 3, (short)4);
        assertFalse( b1.equals( b2 ));
        b2 = new Block(1, (short) 2, 5, (short)4);
        assertFalse( b1.equals( b2 ));
        b2 = new Block(1, (short) 2, 3, (short)5);
        assertFalse( b1.equals( b2 ));
    }
}
