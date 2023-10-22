package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class Latency extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Modify will modify your actual latency. Spoof will just spoof it.")
        .defaultValue(Mode.Spoof)
        .build()
    );

    public final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Amount of latency to add in milliseconds")
            .defaultValue(50)
            .min(0)
            .max(3000)
            .sliderRange(0, 1000)
            .build()
    );

    private final List<PacketEntry> entries = new ArrayList<>();
    private final List<Packet<?>> dontRepeat = new ArrayList<>();

    public Latency() {
        super(Categories.Misc, "latency", "Modify or spoof latency to the server.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!isActive()) return;
        if (!dontRepeat.contains(event.packet) && shouldDelayPacket(event.packet)) {
                event.setCancelled(true);
                entries.add(new PacketEntry(event.packet, delay.get(), System.currentTimeMillis()));
            } else {
                dontRepeat.remove(event.packet);
            }
    }

    private boolean shouldDelayPacket(Packet<?> p) {
        if (mode.get() == Mode.Modify) {
            return true; // Delay all packets
        } else {
            return p instanceof CommonPongC2SPacket || p instanceof KeepAliveC2SPacket; // Only delay pong or keepalive packets
        }
    }

    @Override
    public void onActivate() {
        entries.clear();
        dontRepeat.clear();
    }

    @Override
    public String getInfoString() {
        return delay.get().toString()+ "ms";
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (mc.getNetworkHandler() == null) {
            toggle();
            return;
        }
        long c = System.currentTimeMillis();
        for (PacketEntry entry : entries.toArray(new PacketEntry[0])) {
            if (entry.entryTime + entry.delay <= c) {
                dontRepeat.add(entry.packet);
                entries.remove(entry);
                mc.getNetworkHandler().sendPacket(entry.packet);
            }
        }
    }

    public enum Mode {
        Modify,
        Spoof
    }

    private record PacketEntry(Packet<?> packet, double delay, long entryTime) {

    }
}
