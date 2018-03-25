package org.processmining.framework.models.bpel.util;

/**
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Pair<First, Second> {
    /***/
    public final First first;

    /***/
    public final Second second;

    private int hashCode;

    /**
     * @param first
     * @param second
     */
    protected Pair(First first, Second second) {
        this.first = first;
        this.second = second;
        final int PRIME = 31;
        hashCode = 1;
        hashCode = PRIME * hashCode + ((first == null) ? 0 : first.hashCode());
        hashCode = PRIME * hashCode + ((second == null) ? 0 : second.hashCode());
    }

    public static <S, T> Pair<S, T> create(S first, T second) {
        return new Pair<S, T>(first, second);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "(" + first + "," + second + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            return other.second == null;
        } else return second.equals(other.second);
    }

}
