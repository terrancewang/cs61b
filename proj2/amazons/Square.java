package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static amazons.Utils.*;

/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Terrance Wang
 */
final class Square {
    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
            new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        _str = String.format("%s%d", (char) ('a' + _col), _row + 1);
    }

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        if (this == to) {
            return false;
        } else {
            int ncol = to.col() - this.col();
            int nrow = to.row() - this.row();
            if (ncol == 0 || nrow == 0) {
                return true;
            } else if (Math.abs(nrow / ncol) == 1) {
                return true;
            }
            return false;
        }
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for
     *  northwest. If DIR has another value, return null. Thus, unless the
     *  result is null the resulting square is a queen move away from me. */
    Square queenMove(int dir, int steps) {
        if (dir > 7) {
            return null;
        } else {
            int ncol = this.col() + (DIR[dir][0] * steps);
            int nrow = this.row() + (DIR[dir][1] * steps);
            if (!exists(ncol, nrow)) {
                return null;
            } else {
                return sq(ncol, nrow);
            }
        }
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
        assert isQueenMove(to);
        int[] m = new int[2];
        if (to.row() == this.row()) {
            m[1] = 0;
            m[0] = (to.col() - this.col()) / Math.abs(to.col() - this.col());
        } else if (to.col() == this.col()) {
            m[0] = 0;
            m[1] = (to.row() - this.row()) / Math.abs(to.row() - this.row());
        } else {
            m[0] = (to.col() - this.col()) / Math.abs(to.col() - this.col());
            m[1] = (to.row() - this.row()) / Math.abs(to.row() - this.row());
        }

        int x = -1;
        for (int i = 0; i < DIR.length; i++) {
            if (m[0] == DIR[i][0] && m[1] == DIR[i][1]) {
                x = i;
            }
        }
        return x;
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return sq(row * 10 + col);
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        return sq((Integer.parseInt(posn.substring(1)) - 1) * 10
                + (posn.charAt(0) - 'a'));
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }







}
