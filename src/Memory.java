import java.util.*;

public class Memory {
    private int[] mem;
    public Memory(int words) {
        mem = new int[words];
        reset();
    }

    public void reset() { for(int i=0;i<mem.length;i++) mem[i]=0; }

    public int loadWord(int addrWord) {
        if(addrWord<0 || addrWord>=mem.length) return 0;
        return mem[addrWord];
    }

    public void storeWord(int addrWord, int val) {
        if(addrWord<0 || addrWord>=mem.length) return;
        mem[addrWord]=val;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<mem.length;i++) {
            if(mem[i]!=0) sb.append(String.format("%d: %d\n", i, mem[i]));
        }
        return sb.toString();
    }
}
