package meteordevelopment.meteorclient.systems.modules.crash;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.item.Items;

public class LoginCrash extends Module {
    public LoginCrash() {
        super(Categories.Crash, "login-crash", "Tries to crash the server on login using null packets.");
    }
}
