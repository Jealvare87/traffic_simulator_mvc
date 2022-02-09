package es.ucm.fdi.launcher;

	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.Comparator;

	public class SortedArrayList<E> extends ArrayList<E>{

		private static final long serialVersionUID = 1L;
		private Comparator<E> cmp;
		
		// Este constructor se utiliza cuando pasamos el comparador

		public SortedArrayList(Comparator<E> cmp) {
			super();
			this.cmp = cmp;
		}
		
		// Si no se pasa el comparador se utiliza la interfaz Comparable 
		// con el metodo compareTo().
	    // En la practica hay que comparar eventos por el tiempo y
		// vehiculos por la localizacion. En ambos casos son enteros y el orden
		// es natural. Por eso utilizamos compareTo()

		public SortedArrayList() {
			this.cmp = new Comparator<E>() {

				@SuppressWarnings("unchecked")
				@Override
				public int compare(E o1, E o2) {
					return ((Comparable<E>) o1).compareTo(o2);
				}
			};
		}

		@Override
		public boolean add(E e) {
			int i = 0;

			// get to the first element equals to e ...
			while (i < size() && cmp.compare(e, get(i)) != -1) {
				i++;
			}

			// then skip till the first element greater than e
			while (i < size() && cmp.compare(e, get(i)) == 0) {
				i++;
			}

			super.add(i, e);

			return true;
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			for (E e : c) {
				add(e);
			}
			return true;
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException("Cannot insert to a sorted list");
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException("Cannot insert to a sorted list");
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException("Cannot set an element in a sorted list");
		}

	}



