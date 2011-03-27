package net.juniorbl.jtoyracing.entity.environment;

import javax.swing.ImageIcon;

import net.juniorbl.jtoyracing.enums.GridPosition;
import net.juniorbl.jtoyracing.enums.ResourcesPath;
import net.juniorbl.jtoyracing.util.ModelUtil;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;

/**
 * A kid's room. First level of the game.
 *
 * @version 1.0 Aug 11, 2007
 * @author Carlos Luz Junior
 */
public class KidsRoom extends Node {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4018480325870153949L;

	/**
	 * Scale of the room's texture.
	 */
	private static final Vector3f ROOM_TEXTURE_SCALE = new Vector3f(2f, 2f, 2f);

	/**
	 * Size of the floor.
	 */
	private static final Vector3f FLOOR_SIZE = new Vector3f(9, 0, 5.2f);

	/**
	 * Location of the room.
	 */
	private static final Vector3f LOCATION = new Vector3f(-66, -30, 28);

	/**
	 * Block of terrain where the room is.
	 */
	private TerrainBlock floorBlock;

	/**
	 * Race track of the room.
	 */
	private RaceTrack raceTrack;

	/**
	 * Wall of the room.
	 */
	private Node wall;

	/**
	 * Constructs a Room. It uses physicsSpace states and renderer to build its components.
	 */
	public KidsRoom(PhysicsSpace physicsSpace, TextureState textureState) {
		createFloor(physicsSpace, textureState);
		loadRaceTrack(physicsSpace);
		createWall(physicsSpace);
		createRoomObjects(physicsSpace);
		this.setLocalTranslation(LOCATION);
	}

	/**
	 * Creates the floor of the room.
	 */
	private void createFloor(PhysicsSpace physicsSpace, TextureState textureState) {
		MidPointHeightMap mapHeight = new MidPointHeightMap(32, 5f);
		floorBlock = new TerrainBlock("terrain", mapHeight.getSize(),
				FLOOR_SIZE, mapHeight.getHeightMap(),
				new Vector3f(0, 0, 0), false);
		floorBlock.setModelBound(new BoundingBox());
		floorBlock.updateModelBound();

		StaticPhysicsNode floorNode = physicsSpace.createStaticNode();
		floorNode.attachChild(floorBlock);
		floorNode.generatePhysicsGeometry(true);
		floorNode.setMaterial(Material.WOOD);
		this.attachChild(floorNode);
		loadFloorTexture(textureState);
	}

	private void loadFloorTexture(TextureState textureState) {
		ImageIcon imagemTextura = new ImageIcon(
				KidsRoom.class.getClassLoader().getResource(ResourcesPath.TEXTURE_PATH + "roomFloor.jpg"));
		Texture texture = TextureManager.loadTexture(
				imagemTextura.getImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);
		texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
		texture.setScale(ROOM_TEXTURE_SCALE);
		textureState.setTexture(texture);
		floorBlock.setRenderState(textureState);
	}

	private void loadRaceTrack(PhysicsSpace physicsSpace) {
		raceTrack = new RaceTrack(physicsSpace, getFloorHeight());
		this.attachChild(raceTrack);
	}

	/**
	 * Creates the wall.
	 *
	 * FIXME use 4 Quad.
	 */
	private void createWall(PhysicsSpace physicsSpace) {
		StaticPhysicsNode physicWallProperties = physicsSpace.createStaticNode();
		physicWallProperties.setLocalTranslation(new Vector3f(0, -30, 30));
		physicWallProperties.attachChild(ModelUtil.convertMultipleModelToJME(ResourcesPath.MODELS_PATH + "obj/wall.obj"));
		physicWallProperties.setMaterial(Material.GHOST);
		physicWallProperties.setLocalScale(11f);
		physicWallProperties.generatePhysicsGeometry();
		wall = new Node("wall");
		wall.attachChild(physicWallProperties);
		wall.setLocalTranslation(new Vector3f(67, getFloorHeight() + 30, -28));
		this.attachChild(wall);
	}

	/**
	 * Checks whether a vehicle reached a checkpoint of the race track.
	 */
	public final boolean isVehicleReachedCheckpoint(BoundingVolume vehicleBoundingVolume) {
		return raceTrack.isVehicleReachedCheckpoint(vehicleBoundingVolume);
	}

	/**
	 * Gets the floor height.
	 */
	public final float getFloorHeight() {
		return floorBlock.getHeight(new Vector3f());
	}

	/**
	 * Creates the lego doll in bend three.
	 */
	private void createLegoDoll(PhysicsSpace physicsSpace) {
		StaticPhysicsNode legoDoll = physicsSpace.createStaticNode();
		legoDoll.setModelBound(new BoundingBox());
		legoDoll.setLocalTranslation(new Vector3f(200, getFloorHeight() + 3.9f, 30));
		legoDoll.attachChild(ModelUtil.convertMultipleModelToJME(
				ResourcesPath.MODELS_PATH + "obj/legoDoll.obj"));
		legoDoll.setMaterial(Material.IRON);
		final float legoDollScale = 2.5f;
		legoDoll.setLocalScale(legoDollScale);
		legoDoll.generatePhysicsGeometry();
		this.attachChild(legoDoll);
	}

	/**
	 * Creates the objects that completes the room.
	 *
	 * FIXME don't load the objects as one big model.
	 */
	private void createRoomObjects(PhysicsSpace physicsSpace) {
		StaticPhysicsNode roomObjects = physicsSpace.createStaticNode();
		roomObjects.setModelBound(new BoundingBox());
		roomObjects.setLocalTranslation(new Vector3f(20, getFloorHeight(), 55));
		roomObjects.attachChild(
				ModelUtil.convertMultipleModelToJME(ResourcesPath.MODELS_PATH + "obj/roomObjects.obj"));
		roomObjects.setMaterial(Material.GHOST);
		roomObjects.setLocalScale(1f);
		createLegoDoll(physicsSpace);
		this.attachChild(roomObjects);
	}

	public final Vector3f getGridPosition(GridPosition position) {
		return raceTrack.getGridPosition(position, getFloorHeight());
	}
}
