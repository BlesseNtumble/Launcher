package launchserver.response.profile;

import java.io.IOException;
import java.util.Arrays;

import launcher.helper.VerifyHelper;
import launcher.request.uuid.BatchProfileByUsernameRequest;
import launcher.serialize.HInput;
import launcher.serialize.HOutput;
import launchserver.LaunchServer;
import launchserver.response.Response;

public final class BatchProfileByUsernameResponse extends Response {
    public BatchProfileByUsernameResponse(LaunchServer server, long id, HInput input, HOutput output) {
        super(server, id, input, output);
    }

    @Override
    public void reply() throws IOException {
        int length = input.readLength(BatchProfileByUsernameRequest.MAX_BATCH_SIZE);
        String[] usernames = new String[length];
        String[] clients = new String[length];
        for (int i = 0; i < usernames.length; i++) {
            usernames[i] = VerifyHelper.verifyUsername(input.readString(64));
            clients[i] = input.readString(64);
        }
        debug("Usernames: " + Arrays.toString(usernames));

        // Respond with profiles array
        for (int i = 0; i < usernames.length; i++) {
            ProfileByUsernameResponse.writeProfile(server, output, usernames[i],clients[i]);
        }
    }
}
