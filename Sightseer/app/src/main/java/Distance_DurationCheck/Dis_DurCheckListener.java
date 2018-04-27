package Distance_DurationCheck;

import java.util.List;

public interface Dis_DurCheckListener {
    void onDis_DurStart();
    void onDis_DurSuccess(List<Routes> routes);
}