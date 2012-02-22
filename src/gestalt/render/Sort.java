/*
 * Gestalt
 *
 * Copyright (C) 2012 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.render;


/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version     1.6f, 31 Jan 1995
 */
/**
 * 19 Feb 1996: Fixed to avoid infinite loop discoved by Paul Haeberli.
 *              Misbehaviour expressed when the pivot element was not unique.
 *              -Jason Harrison
 *
 * 21 Jun 1996: Modified code based on comments from Paul Haeberli, and
 *              Peter Schweizer (Peter.Schweizer@mni.fh-giessen.de).
 *              Used Daeron Meyer's (daeron@geom.umn.edu) code for the
 *              new pivoting code. - Jason Harrison
 *
 * 09 Jan 1998: Another set of bug fixes by Thomas Everth (everth@wave.co.nz)
 *              and John Brzustowski (jbrzusto@gpu.srv.ualberta.ca).
 *
 * 26 Apr 2004: Added the Drawable interface to sort object lists
 *              by Dennis Paul (d3@m-d3.com)
 *
 */


public class Sort {

    public static void qSort(Drawable[] a) {
        qSort(a, 0, a.length - 1);
    }


    public static void qSort(Drawable[] a, int lo0, int hi0) {

        int lo = lo0;
        int hi = hi0;
        if (lo >= hi) {
            return;
        } else if (lo == hi - 1) {
            /*
             *  sort a two element list by swapping if necessary
             */
            if (a[lo].getSortValue() > a[hi].getSortValue()) {
                Drawable T = a[lo];
                a[lo] = a[hi];
                a[hi] = T;
            }
            return;
        }

        /*
         *  Pick a pivot and move it out of the way
         */
        Drawable pivot = a[ (lo + hi) / 2];
        a[ (lo + hi) / 2] = a[hi];
        a[hi] = pivot;

        while (lo < hi) {
            /*
             *  Search forward from a[lo] until an element is found that
             *  is greater than the pivot or lo >= hi
             */
            while (a[lo].getSortValue() <= pivot.getSortValue() && lo < hi) {
                lo++;
            }

            /*
             *  Search backward from a[hi] until element is found that
             *  is less than the pivot, or lo >= hi
             */
            while (pivot.getSortValue() <= a[hi].getSortValue() && lo < hi) {
                hi--;
            }

            /*
             *  Swap elements a[lo] and a[hi]
             */
            if (lo < hi) {
                Drawable T = a[lo];
                a[lo] = a[hi];
                a[hi] = T;
            }

        }

        /*
         *  Put the median in the "center" of the list
         */
        a[hi0] = a[hi];
        a[hi] = pivot;

        /*
         *  Recursive calls, elements a[lo0] to a[lo-1] are less than or
         *  equal to pivot, elements a[hi+1] to a[hi0] are greater than
         *  pivot.
         */
        qSort(a, lo0, lo - 1);
        qSort(a, hi + 1, hi0);
    }


    public static void shellSort(Drawable[] a, int theStart, int theEnd) {
        int h = 1;
        int _myListLength = theEnd - theStart;
        /*
         * find the largest h value possible
         */
        while ( (h * 3 + 1) < _myListLength) {
            h = 3 * h + 1;
        }

        /*
         * while h remains larger than 0
         */
        while (h > 0) {
            /*
             * for each set of elements (there are h sets)
             */
            for (int i = h - 1; i < _myListLength; i++) {
                /*
                 * pick the last element in the set
                 */
                Drawable B = a[i + theStart];
                int j = i;
                /*
                 * compare the element at B to the one before it in the set
                 * if they are out of order continue this loop, moving
                 * elements "back" to make room for B to be inserted.
                 */
                for (j = i; (j >= h) && (a[ (j - h) + theStart].getSortValue() > B.getSortValue()); j -= h) {
                    a[j + theStart] = a[ (j - h) + theStart];
                }
                /*
                 *  insert B into the correct place
                 */
                a[j + theStart] = B;
            }
            /*
             * all sets h-sorted, now decrease set size
             */
            h = h / 3;
        }
    }


    public static void bubbleSort(Drawable[] list) {
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < list.length - 1; ++i) {
                if (list[i].getSortValue() > list[i + 1].getSortValue()) {
                    Drawable temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }
}
