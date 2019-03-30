package amazons;

import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Stack;
import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Terrance Wang
 */
class Board {
    /** Number of moves elapsed on board.*/
    private int _numMoves;

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;

    /** Array of pieces on board.*/
    private Piece[][] _pieces;

    /**Keeps track of all the previous moves.*/
    private Stack<Move> _moves;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        init();
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        _numMoves = model._numMoves;
        _moves = model._moves;
        _turn = model._turn;
        _winner = model._winner;
        for (int i = 0; i < _pieces.length; i++) {
            for (int j = 0; j < _pieces[i].length; j++) {
                _pieces[i][j] = model._pieces[i][j];
            }
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        _numMoves = 0;
        _moves = new Stack<Move>();
        _pieces = new Piece[SIZE][SIZE];
        _turn = WHITE;
        _winner = EMPTY;
        for (int i = 0; i < _pieces.length; i++) {
            for (int j = 0; j < _pieces[i].length; j++) {
                _pieces[i][j] = EMPTY;
            }
        }

        _pieces[3][0] = WHITE;
        _pieces[6][0] = WHITE;
        _pieces[0][3] = WHITE;
        _pieces[9][3] = WHITE;
        _pieces[0][6] = BLACK;
        _pieces[9][6] = BLACK;
        _pieces[3][9] = BLACK;
        _pieces[6][9] = BLACK;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (_winner == EMPTY) {
            return null;
        } else {
            return _winner;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _pieces[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        _pieces[col][row] = p;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    @Override
    public String toString() {
        String b = "";
        for (int i = (SIZE - 1); i >= 0; i--) {
            b += "  ";
            for (int j = 0; j < SIZE; j++) {
                b += " ";
                b += _pieces[j][i].toString();
            }
            b += "\n";
        }
        return b;
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if ((_pieces[to.col()][to.row()] != EMPTY && asEmpty != to)
                || !from.isQueenMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int i = 1;
        Square next = from.queenMove(dir, i);
        while (next != to) {
            if (next != asEmpty && _pieces[next.col()][next.row()] != EMPTY) {
                return false;
            }
            i += 1;
            next = from.queenMove(dir, i);
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return _turn == _pieces[from.col()][from.row()];
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && isUnblockedMove(to, spear, from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (!isLegal(from, to, spear)) {
            System.out.println(mv(from, to, spear));
            throw new Error("illegal move!!");
        }
        _numMoves += 1;
        put(_pieces[from.col()][from.row()], to);
        put(EMPTY, from);
        put(SPEAR, spear);
        _moves.push(mv(from, to, spear));

        Piece opp;
        if (_turn == WHITE) {
            opp = WHITE;
            _turn = BLACK;
        } else {
            opp = BLACK;
            _turn = WHITE;
        }
        if (!legalMoves(_turn).hasNext()) {
            _winner = opp;
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (!_moves.isEmpty()) {
            Move un = _moves.pop();
            if (_turn == WHITE) {
                _turn = BLACK;
            } else {
                _turn = WHITE;
            }
            put(EMPTY, un.to());
            put(EMPTY, un.spear());
            put(_turn, un.from());
            _numMoves -= 1;
            _winner = EMPTY;
        }
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square n = _from.queenMove(_dir, _steps);
            toNext();
            return n;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            _steps++;
            while (_dir != 8 && (_from.queenMove(_dir, _steps) == null
                    || (_from.queenMove(_dir, _steps) != _asEmpty
                    && get(_from.queenMove(_dir, _steps)) != EMPTY))) {
                _steps = 1;
                _dir += 1;
                if (_dir == 8) {
                    break;
                }
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            Iterator<Square> sq = Square.iterator();
            ArrayList<Square> q = new ArrayList<>();
            _fromPiece = side;
            notCompleted = false;
            while (sq.hasNext()) {
                Square a = sq.next();
                if (get(a) == side) {
                    q.add(a);
                }
            }
            _spearThrows = NO_SQUARES;
            _startingSquares = q.iterator();
            _start = _startingSquares.next();
            _pieceMoves = new ReachableFromIterator(_start, null);
            while (!_pieceMoves.hasNext() && _startingSquares.hasNext()) {
                _start = _startingSquares.next();
                _pieceMoves = reachableFrom(_start, null);
            }
            _nextSquare = _pieceMoves.next();

            if (_nextSquare != null) {
                _spearThrows = reachableFrom(_nextSquare, _start);
                notCompleted = true;
            }
            toNext();
        }

        @Override
        public boolean hasNext() {
            return notCompleted || _startingSquares.hasNext()
                    || _pieceMoves.hasNext() || _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            Move m = mv(_start, _nextSquare, _spear);
            if (!(_startingSquares.hasNext()
                    || _pieceMoves.hasNext() || _spearThrows.hasNext())) {
                notCompleted = false;
            } else {
                toNext();
            }
            return m;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (!_spearThrows.hasNext()) {
                if (_pieceMoves.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                    _spear = _spearThrows.next();
                } else if (_startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                    _pieceMoves = reachableFrom(_start, null);
                    while (_startingSquares.hasNext()
                            && !_pieceMoves.hasNext()) {
                        _start = _startingSquares.next();
                        _pieceMoves = reachableFrom(_start, null);
                    }
                    if (!_pieceMoves.hasNext()) {
                        _spearThrows = NO_SQUARES;
                        notCompleted = false;
                        return;
                    }
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                    _spear = _spearThrows.next();
                }
            } else {
                _spear = _spearThrows.next();
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
        /** next spear to place.*/
        private Square _spear;
        /** Checks to see if very last spear has been displayed.*/
        private boolean notCompleted;
    }

}
