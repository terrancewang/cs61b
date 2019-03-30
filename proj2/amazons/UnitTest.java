package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;
import static amazons.Square.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();

        b.put(BLACK, Square.sq(3, 5));

        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    @Test
    public void strSquareConstructor() {
        Square a = sq("a1");
        System.out.println(a);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    @Test
    public void testCopy() {
        Board b = new Board();
        makeSmile(b);
        Board s = new Board(b);
        System.out.println(s.toString());
        System.out.println(b.toString());
        assertEquals(s.toString(), b.toString());
    }

    @Test
    public void testDirection() {
        Board b = new Board();
        Square a = sq(5, 5);
        assertEquals(1, a.direction(sq(8, 8)));
    }

    @Test
    public void testMakeMove() {
        Board b = new Board();
        System.out.println(b);
        b.makeMove(sq(3, 0), sq(4, 0), sq(3, 0));
        System.out.println(b);
    }

    @Test
    public void testUndo() {
        Board a = new Board();
        Board b = new Board();
        System.out.println(b);
        b.makeMove(sq(3, 0), sq(5, 2), sq(5, 7));
        System.out.println(b);
        b.undo();
        System.out.println(b);
        assertEquals(a.toString(), b.toString());
    }

    @Test
    public void testUnblocked() {
        Board b = new Board();
        assertEquals(false, b.isUnblockedMove(sq(3, 0), sq(3, 9), sq(3, 5)));
        System.out.println(b.toString());
        assertEquals(true, b.isUnblockedMove(sq(3, 0), sq(3, 1), sq(3, 2)));
        assertEquals(false, b.isUnblockedMove(sq(0, 0), sq(5, 0), null));
        assertEquals(false, b.isUnblockedMove(sq(3, 0), sq(4, 5), null));
        assertEquals(true, b.isUnblockedMove(sq(6, 0), sq(1, 5), null));
        assertEquals(true, b.isUnblockedMove(sq(3, 0), sq(3, 8), sq(3, 0)));
        assertEquals(true, b.isUnblockedMove(sq(3, 0), sq(7, 0), sq(6, 0)));
    }

    @Test
    public void testIsLegal() {
        Board b = new Board();
        buildBoard(b, reachableFromTestBoard);
        assertEquals(true, b.isLegal(sq(5, 4), sq(5, 5), sq(5, 4)));
        b.makeMove(sq(5, 4), sq(5, 5), sq(5, 4));
        System.out.println(b);
        assertEquals(false, b.isLegal(sq(5, 5), sq(5, 4), sq(5, 4)));
        assertEquals(false, b.isLegal(sq(5, 5), sq(6, 4), sq(5, 4)));
        assertEquals(false, b.isLegal(sq(3, 0), sq(7, 0), sq(6, 0)));
    }

    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, reachableFromTestBoard);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(reachableFromTestSquares.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(reachableFromTestSquares.size(), numSquares);
        assertEquals(reachableFromTestSquares.size(), squares.size());
    }

    @Test
    public void testReachableFrom2() {
        Board b = new Board();
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(3, 0), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(20, numSquares);
        assertEquals(20, squares.size());
    }

    @Test
    public void testReachableSpear() {
        Board b = new Board();
        Set<Move> squares = new HashSet<>();
        int numMoves = 0;
        Iterator<Square> reachableFrom =
                b.reachableFrom(Square.sq(3, 0), Square.sq(3, 0));
        while (reachableFrom.hasNext()) {
            Move m = Move.mv(Square.sq(3, 0),
                    reachableFrom.next(), Square.sq(3, 0));
            numMoves += 1;
            squares.add(m);
        }
        assertEquals(20, numMoves);
        System.out.println(squares);
    }

    @Test
    public void testSquare() {
        Board b = new Board();
        System.out.println(sq(99));
        System.out.println(sq(99).index());
        System.out.println(sq(0));
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        int numMoves = 0;
        Iterator<Move> legalMoves = b.legalMoves(Piece.BLACK);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            numMoves += 1;
        }
        System.out.println(b);
        System.out.println(numMoves);
        assertEquals(2176, numMoves);
    }

    @Test
    public void testLegalMoves1() {
        Board b = new Board();
        buildBoard(b, legal1);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            System.out.println(m);
            numMoves += 1;
            moves.add(m);
        }
        System.out.println(b);
        System.out.println(numMoves);
    }

    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static Set<Square> reachableFromTestSquares =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }





    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static Piece[][] legal1 =
    {
            { S, S, S, B, W, S, E, E, S, W },
            { S, S, S, S, S, S, E, E, E, S },
            { S, S, S, S, S, E, E, E, E, E },
            { E, S, S, E, E, E, E, E, E, E },
            { S, S, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { S, S, E, E, E, E, E, E, S, S },
            { S, S, E, E, E, E, E, E, S, S },
    };

    static Piece[][] legal2 =
    {
            { E, E, E, B, E, E, B, W, W, W },
            { E, E, E, E, E, E, S, S, S, S },
            { E, E, E, E, E, E, E, E, E, E },
            { B, E, E, E, E, E, E, E, E, B },
            { E, E, E, E, E, E, E, E, E, E },
            { S, S, E, E, E, E, E, E, E, E },
            { W, S, E, E, E, E, E, E, E, E },
            { S, S, E, E, E, E, E, E, E, E },
            { E, E, S, S, S, S, E, S, E, E },
            { E, B, S, W, S, S, W, S, E, E },
    };

    static Piece[][] reachableFromTestBoard =
    {
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, W, W },
            { E, E, E, E, E, E, E, S, E, S },
            { E, E, E, S, S, S, S, E, E, S },
            { E, E, E, S, E, E, E, E, B, E },
            { E, E, E, S, E, W, E, E, B, E },
            { E, E, E, S, S, S, B, W, B, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E }
    };

}


