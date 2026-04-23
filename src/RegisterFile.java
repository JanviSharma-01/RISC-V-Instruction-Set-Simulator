import java.util.stream.*;
public class RegisterFile {
    private int[] regs = new int[32];

    public RegisterFile() {
        reset();
    }

    public void reset() {
        for(int i=0;i<32;i++) regs[i]=0;
    }

    public int read(int idx) {
        if(idx==0) return 0;
        return regs[idx];
    }

    public void write(int idx, int val) {
        if(idx==0) return;
        regs[idx]=val;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<32;i++) {
            sb.append(String.format("x%02d = %d\n", i, regs[i]));
        }
        return sb.toString();
    }
}
