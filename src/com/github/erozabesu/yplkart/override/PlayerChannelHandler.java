package com.github.erozabesu.yplkart.override;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class PlayerChannelHandler extends ChannelDuplexHandler {

    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            /*
             * プレイヤーがLivingEntityに姿を変えていた場合、LivingEntityのMetadataパケットに
             * EntityHuman特有のデータが送信されクライアントがクラッシュしてしまうため該当データを削除する
             * EntityHuman特有のデータとは、PacketPlayOutEntityMetadata.bに格納されている
             * List<DataWatcher.WatchableObject>の中の特定の4つのデータ
             * DataWatcher.WatchableObject.a()メソッドを実行するとWatchableObjectのindexが取得できるので
             * indexが10、16、17、18に一致したものを削除する
             * 各データが何を指すかはhttp://wiki.vg/Entities#Entity_Metadata_FormatのHumanの項目を参照。
             */
            if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) {
                int id = (Integer) ReflectionUtil.getFieldValue(msg, "a");
                if (NettyListener.playerEntityId.get(id) == null) {
                    super.write(ctx, msg, promise);
                    return;
                } else {
                    Player p = Bukkit.getPlayer(UUID.fromString(NettyListener.playerEntityId.get(id)));
                    Racer r = RaceManager.getRace(p);

                    if (r.getCharacter() == null) {
                        super.write(ctx, msg, promise);
                    } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        super.write(ctx, msg, promise);
                    } else {
                        List<Object> watchableobject = (List<Object>) ReflectionUtil.getFieldValue(msg, "b");
                        Iterator i = watchableobject.iterator();
                        while (i.hasNext()) {
                            Object w = i.next();
                            int index = (Integer) w.getClass().getMethod("a").invoke(w);
                            if (index == 10 || index == 16 || index == 17 || index == 18) {
                                i.remove();
                            }
                        }
                    }
                }

            //Human以外のキャラクターを選択している場合パケットを偽装
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")) {
                Player p = Bukkit.getPlayer((UUID) Util.getFieldValue(msg, "b"));
                Racer r = RaceManager.getRace(p);

                if (r.getCharacter() == null) {
                    super.write(ctx, msg, promise);
                } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                    super.write(ctx, msg, promise);
                } else {
                    super.write(ctx, PacketUtil.getDisguisePacket(p, r.getCharacter()), promise);
                }
                return;

            //Human以外のキャラクターを選択している場合、装備の情報を全て破棄する
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityEquipment")) {
                int id = (Integer) ReflectionUtil.getFieldValue(msg, "a");
                if (NettyListener.playerEntityId.get(id) == null) {
                    super.write(ctx, msg, promise);
                    return;
                } else {
                    Player player = Bukkit.getPlayer(UUID.fromString(NettyListener.playerEntityId.get(id)));
                    Racer r = RaceManager.getRace(player);

                    if (r.getCharacter() == null) {
                        super.write(ctx, msg, promise);
                    } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        super.write(ctx, msg, promise);
                    } else {
                        // Do nothing
                    }
                }
            } else {
                super.write(ctx, msg, promise);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
