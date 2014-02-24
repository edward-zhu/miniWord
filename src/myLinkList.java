/**
 * Created by EdwardZhu on 14-2-21.
 */
class Node<T> {
	T data;
	private Node<T> next;
	private Node<T> prev;

	public Node<T> getPrev() {
		return prev;
	}

	public void setPrev(Node<T> prev) {
		this.prev = prev;
	}

	Node(T data) {
		this.data = data;
		this.setNext(null);
		this.setPrev(null);
	}

	public Node<T> getNext() {
		return next;
	}

	public void setNext(Node<T> next) {
		this.next = next;
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}

public class myLinkList<T> {
	private Node<T> head, tail;

	myLinkList() {
		head = null;
		tail = null;
	}

	void addLast(Node<T> node) {
		if (head != null) {
			tail.setNext(node);
			node.setPrev(tail);
			tail = node;
		}
		else {
			head = tail = node;
		}
	}

	public void addLast(T data) {
		Node<T> node = new Node<T>(data);
		addLast(node);
	}

	void addFirst(Node<T> node) {
		if (head == null) {
			head = node;
			tail = node;
		}
		else {
			node.setNext(head);
			head.setPrev(node);
			head = node;
		}
	}

	public void addFirst(T data) {
		Node<T> node = new Node<T>(data);
		addFirst(node);
	}

	public void add(int index, T data) {
		int i = 0;
		Node<T> h = head, node = new Node<T>(data);
		for (;h.hasNext() && i < index;h = h.getNext()) i++;
		if (h == head) {
			addFirst(node);
		} else {
			h = h.getPrev();
			if (h == null) {
				if (head == null) {
					head = tail = node;
				}
				else {
					head.setPrev(node);
					node.setNext(head);
					head = node;
				}
			}
			else if(h == tail){
				node.setPrev(tail);
				tail.setNext(node);
				tail = node;
			}
			else {
				node.setNext(h.getNext());
				h.setNext(node);
				node.setPrev(h);
			}
		}
	}

	public void remove(int index) {
		Node<T> h = head, t;
		for (int i = 0;h.hasNext() && i < index;h = h.getNext()) i++;
		t = h.getNext();
		h.setNext(t.getNext());
		t.getNext().setPrev(h);

	}

	public void print() {
		int i = 0;
		for (Node<T> h = head;h != null;h = h.getNext()) {

			System.out.print(String.valueOf(i++) + " " + h.getData());
			if (h.getPrev() != null) {
				System.out.print(" <- " + h.getPrev().getData());
			}
			if (h.getNext() != null) {
				System.out.print(" -> " + h.getNext().getData());
			}
			System.out.println();
		}
	}
}
