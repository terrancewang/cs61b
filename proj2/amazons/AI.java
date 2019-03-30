package amazons;
import java.util.Iterator;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Terrance Wang
 */
class AI extends Player {

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Final int for adaptive depth search.*/
    private static final int MIDDLE = 35;

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }


    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        if (sense == 1) {
            Iterator<Move> w = board.legalMoves(WHITE);
            while (w.hasNext()) {
                Move n = w.next();
                board.makeMove(n);
                int nextVal = findMove(board, depth - 1,
                        false, -1, alpha, beta);
                board.undo();
                if (alpha < nextVal) {
                    alpha = nextVal;
                    if (saveMove) {
                        _lastFoundMove = n;
                    }
                }

                if (alpha > beta) {
                    return alpha;
                }
            }
            return alpha;
        } else {
            Iterator<Move> bl = board.legalMoves(BLACK);
            while (bl.hasNext()) {
                Move n = bl.next();
                board.makeMove(n);
                int nextVal = findMove(board, depth - 1,
                        false, 1, alpha, beta);
                board.undo();
                if (beta > nextVal) {
                    beta = nextVal;
                    if (saveMove) {
                        _lastFoundMove = n;
                    }
                }

                if (beta < alpha) {
                    return beta;
                }
            }
            return beta;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        Iterator<Move> m = board.legalMoves(_myPiece);
        int N = 0;
        while (m.hasNext()) {
            m.next();
            N++;
        }
        if (N > 1000) {
            return 1;
        } else if (N > 100) {
            return 2;
        } else if (N > MIDDLE) {
            return 3;
        } else if (N > 15) {
            return 4;
        } else {
            return 5;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        Iterator validWhite = board.legalMoves(WHITE);
        int numWhite = 0;
        while (validWhite.hasNext()) {
            numWhite += 1;
            validWhite.next();
        }
        return numWhite;
    }
}
