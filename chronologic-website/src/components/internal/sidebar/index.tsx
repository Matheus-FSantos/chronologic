import { ChartColumnStacked, Server, SquareTerminal, VectorSquare } from "lucide-react";
import { Sidebar, SidebarContent, SidebarGroup, SidebarHeader, SidebarMenuItem } from "../../ui/sidebar";
import { cn } from "@/lib/utils";
import { Badge } from "../badge";
import { SidebarItem } from "./sidebar-items";

type AppSidebarProps = {
  activedItem: string
}

const AppSidebar = ({activedItem}: AppSidebarProps): React.ReactElement => {
  return (
    <Sidebar side="left" collapsible="icon">
      <SidebarHeader className="mt-1 flex flex-col gap-4 items-start">
        <div className="w-full flex flex-row gap-2 items-center">
          <div className={cn("bg-primary-foreground/60 p-1.5 radius-sm")}>
            <SquareTerminal size={20} className="text-primary"/>
          </div>
          <div className="flex flex-col items-center justify-center gap-0 leading-4">
            <h1 className="font-heading font-bold text-md text-primary">Chronologic</h1>
            <span className="text-[10px] text-white/60">v0.1.0-SNAPSHOOT</span>
          </div>
        </div>
        <div className="flex flex-row gap-2">
          <Badge id="mvp-badge" label="v0.1.0-MVP" muted/>
          <Badge id="admin-badge" label="admin" color="violet-500"/>
          <Badge id="test-badge" label="test" color="orange-400"/>
        </div>
        <div className="w-full h-12 cursor-default flex flex-row gap-2 items-center justify-center bg-primary-foreground border border-primary">
          <span className="relative flex size-2">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-primary opacity-75"></span>
            <span className="relative inline-flex size-2 rounded-full bg-primary"></span>
          </span>
          <span className="font-semibold text-primary">LIVE_MODE</span>
        </div>
      </SidebarHeader>
      <SidebarContent className="pt-1">
        <SidebarGroup>
          <SidebarMenuItem>
            <SidebarItem id="dashboard-page" label="Dashboard" activedItem={activedItem}>
              <Server size={16} strokeWidth={2.5}/>
            </SidebarItem>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarItem id="metrics-page" label="Metrics" activedItem={activedItem}>
              <ChartColumnStacked size={16} strokeWidth={2.5}/>
            </SidebarItem>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarItem id="traces-page" label="Traces" activedItem={activedItem}>
              <VectorSquare size={16} strokeWidth={2.5} />
            </SidebarItem>
          </SidebarMenuItem>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}

export { AppSidebar };