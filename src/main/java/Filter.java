
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import java.awt.*;

public class Filter {

    public Lighting filterColor(Color color) {
        Lighting lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);
        lighting.setLight(new Light.Distant(45, 45, javafx.scene.paint.Color.GREEN));

        return lighting;
    }

}