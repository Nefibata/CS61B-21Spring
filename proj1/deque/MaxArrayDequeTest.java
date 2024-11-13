package deque;

import org.junit.Test;


import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void MaxValueTest(){
        MaxArrayDeque <Integer> maxArrayDeque =new MaxArrayDeque<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1>o2)return 1;
                if (o1<o2)return -1;
                return 0;
            }
        });
        for (int i = 0; i < 10000; i++) {
            maxArrayDeque.addLast(i);
        }
        assertEquals(9999,(int)maxArrayDeque.max());

    }
}
